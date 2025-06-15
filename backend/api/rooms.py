from flask import Blueprint, request, jsonify
from supabase_client import supabase 
from decorators import token_required
from datetime import datetime
import secrets
import string
import uuid
import os

rooms_bp = Blueprint("rooms", __name__, url_prefix="/api")

def generate_code(length=8):
    return ''.join(secrets.choice(string.ascii_uppercase + string.digits) for _ in range(length))


@rooms_bp.route('/rooms/my-rooms', methods=['GET'])
@token_required
def get_my_rooms(current_user_id):
    try:
        # Rooms where user is the owner
        owned_rooms = (
            supabase
            .table('rooms')
            .select('*, users!rooms_owner_id_fkey(user_name, full_name)')
            .eq('owner_id', current_user_id)
            .execute()
        )

        # Rooms where user is a member
        member_rooms = (
            supabase
            .table('room_members')
            .select('rooms!room_members_room_id_fkey(*, users!rooms_owner_id_fkey(user_name, full_name))')
            .eq('user_id', current_user_id)
            .execute()
        )
        member_rooms_data = [item['rooms'] for item in member_rooms.data]

        # Combine and deduplicate
        all_rooms = owned_rooms.data + member_rooms_data
        unique_rooms = {room['room_id']: room for room in all_rooms}.values()

        result_rooms = []
        for room in unique_rooms:
            try:
                members_resp = (
                    supabase
                    .table('room_members')
                    .select('users(user_id, user_name, full_name, avatar_url)')
                    .eq('room_id', room['room_id'])
                    .execute()
                )
                room['members'] = [m['users'] for m in members_resp.data]
            except Exception:
                room['members'] = []

            avatar_path = room.get('avatar_url')
            if avatar_path and not avatar_path.startswith('http'):
                room['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)

            result_rooms.append(room)

        return jsonify(result_rooms)
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@rooms_bp.route('/rooms/public', methods=['GET'])
@token_required
def get_public_rooms(current_user_id):
    """Return public rooms that the current user has not joined."""
    try:
        member_resp = (
            supabase
            .table('room_members')
            .select('room_id')
            .eq('user_id', current_user_id)
            .execute()
        )
        joined_ids = {item['room_id'] for item in member_resp.data}

        owned_resp = (
            supabase
            .table('rooms')
            .select('room_id')
            .eq('owner_id', current_user_id)
            .execute()
        )
        joined_ids.update({item['room_id'] for item in owned_resp.data})

        response = (
            supabase
            .table('rooms')
            .select('*, users!rooms_owner_id_fkey(user_name, full_name)')
            .eq('room_mode', 'public')
            .execute()
        )

        result_rooms = []
        for room in response.data:
            if room['room_id'] in joined_ids:
                continue
            expired_at = room.get('expired_at')
            if expired_at:
                try:
                    exp = datetime.fromisoformat(expired_at.replace('Z', '+00:00'))
                    if exp < datetime.utcnow():
                        continue
                except Exception:
                    pass
            try:
                members_resp = (
                    supabase
                    .table('room_members')
                    .select('users(user_id, user_name, full_name, avatar_url)')
                    .eq('room_id', room['room_id'])
                    .execute()
                )
                room['members'] = [m['users'] for m in members_resp.data]
            except Exception:
                room['members'] = []

            avatar_path = room.get('avatar_url')
            if avatar_path and not avatar_path.startswith('http'):
                room['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)

            result_rooms.append(room)

        return jsonify(result_rooms)
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@rooms_bp.route('/rooms', methods=['POST'])
@token_required
def create_room(current_user_id):
    try:
        if request.content_type and request.content_type.startswith('multipart/form-data'):
            data = request.form.to_dict()
            avatar_file = request.files.get('avatar')
        else:
            data = request.get_json() or {}
            avatar_file = None

        if not data.get('room_name'):
            return jsonify({'error': 'Room name is required'}), 400

        invite_code = generate_code()
        room_data = {
            'owner_id': current_user_id,
            'room_name': data['room_name'],
            'description': data.get('description'),
            'invite_code': invite_code,
            'expired_at': data.get('expired_at'),
            'room_mode': data.get('room_mode', 'public')
        }

        if avatar_file:
            ext = os.path.splitext(avatar_file.filename)[1] or '.jpg'
            unique_name = f"room_{current_user_id}_{uuid.uuid4().hex}{ext}"
            file_bytes = avatar_file.read()
            supabase.storage.from_('avatars').upload(unique_name, file_bytes, {
                'content-type': avatar_file.mimetype
            })
            room_data['avatar_url'] = unique_name

        response = supabase.table('rooms').insert(room_data).execute()
        room = response.data[0]

        avatar_path = room.get('avatar_url')
        if avatar_path and not avatar_path.startswith('http'):
            room['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)

        # Add owner as member
        supabase.table('room_members').insert({
            'room_id': room['room_id'],
            'user_id': current_user_id
        }).execute()

        return jsonify(room), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@rooms_bp.route('/rooms/<room_id>', methods=['GET'])
@token_required
def get_room(current_user_id, room_id):
    try:
        # Kiểm tra xem user có phải là thành viên của phòng không
        member_check = (
            supabase
            .table('room_members')
            .select('*')
            .eq('room_id', room_id)
            .eq('user_id', current_user_id)
            .execute()
        )
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403

        # Lấy thông tin phòng, đồng thời join với bảng users qua khóa ngoại rooms_owner_id_fkey
        response = (
            supabase
            .table('rooms')
            .select('*, users!rooms_owner_id_fkey(user_name, full_name)')
            .eq('room_id', room_id)
            .execute()
        )

        if not response.data:
            return jsonify({'error': 'Room not found'}), 404

        room = response.data[0]
        avatar_path = room.get('avatar_url')
        if avatar_path and not avatar_path.startswith('http'):
            room['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)

        return jsonify(room)

    except Exception as e:
        return jsonify({'error': str(e)}), 500


@rooms_bp.route('/rooms/<room_id>', methods=['PUT'])
@token_required
def update_room(current_user_id, room_id):
    try:
        # Check if user is owner
        room_check = supabase.table('rooms').select('owner_id').eq('room_id', room_id).execute()
        if not room_check.data or room_check.data[0]['owner_id'] != current_user_id:
            return jsonify({'error': 'Access denied'}), 403
        
        data = request.get_json()
        allowed_fields = ['room_name', 'description', 'expired_at']
        update_data = {k: v for k, v in data.items() if k in allowed_fields}
        
        if not update_data:
            return jsonify({'error': 'No valid fields to update'}), 400
        
        response = supabase.table('rooms').update(update_data).eq('room_id', room_id).execute()
        return jsonify(response.data[0])
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@rooms_bp.route('/rooms/<room_id>', methods=['DELETE'])
@token_required
def delete_room(current_user_id, room_id):
    try:
        # Check if user is owner
        room_check = supabase.table('rooms').select('owner_id').eq('room_id', room_id).execute()
        if not room_check.data or room_check.data[0]['owner_id'] != current_user_id:
            return jsonify({'error': 'Access denied'}), 403
        
        supabase.table('room_members').delete().eq('room_id', room_id).execute()

        # Xóa phòng
        supabase.table('rooms').delete().eq('room_id', room_id).execute()

        return jsonify({'message': 'Room deleted successfully'})
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@rooms_bp.route('/rooms/join', methods=['POST'])
@token_required
def join_room_by_code(current_user_id):
    try:
        data = request.get_json()
        invite_code = data.get('invite_code')
        
        if not invite_code:
            return jsonify({'error': 'Invite code is required'}), 400
        
        # Find room by invite code
        room_response = supabase.table('rooms').select('*').eq('invite_code', invite_code).execute()
        if not room_response.data:
            return jsonify({'error': 'Invalid invite code'}), 404
        
        room = room_response.data[0]

        avatar_path = room.get('avatar_url')
        if avatar_path and not avatar_path.startswith('http'):
            room['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)
        
        # Check if room is expired
        if room['expired_at'] and datetime.fromisoformat(room['expired_at'].replace('Z', '+00:00')) < datetime.utcnow():
            return jsonify({'error': 'Invite code has expired'}), 400
        
        # Check if already member
        member_check = supabase.table('room_members').select('*').eq('room_id', room['room_id']).eq('user_id', current_user_id).execute()
        if member_check.data:
            return jsonify({'error': 'Already a member of this room'}), 400
        
        # Add as member
        supabase.table('room_members').insert({
            'room_id': room['room_id'],
            'user_id': current_user_id
        }).execute()
        
        # Add to history
        supabase.table('room_history').insert({
            'room_id': room['room_id'],
            'user_id': current_user_id
        }).execute()
        
        return jsonify({'message': 'Joined room successfully', 'room': room})
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@rooms_bp.route('/rooms/<room_id>/members', methods=['GET'])
@token_required
def get_room_members(current_user_id, room_id):
    try:
        # Check if user is member
        member_check = supabase.table('room_members').select('*').eq('room_id', room_id).eq('user_id', current_user_id).execute()
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403
        
        response = supabase.table('room_members').select('*, users(user_id, user_name, full_name, avatar_url)').eq('room_id', room_id).execute()
        return jsonify(response.data)
    except Exception as e:
        return jsonify({'error': str(e)}), 500