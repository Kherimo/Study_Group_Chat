from flask import Blueprint, request, jsonify
from supabase_client import supabase
from decorators import token_required
from datetime import datetime
import secrets
import string
import uuid
import os

room_messages_bp = Blueprint("room_messages", __name__, url_prefix="/api")

# Lay message cua room theo room id
@room_messages_bp.route('/rooms/<room_id>/messages', methods=['GET'])
@token_required
def get_room_messages(current_user_id, room_id):
    try:
        # Check if user is member
        member_check = supabase.table('room_members').select('*').eq('room_id', room_id).eq('user_id', current_user_id).execute()
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403
        
        page = int(request.args.get('page', 1))
        limit = int(request.args.get('limit', 50))
        offset = (page - 1) * limit
        
        response = supabase.table('room_messages').select('*, users(user_name, full_name, avatar_url)').eq('room_id', room_id).order('sent_at', desc=False).range(offset, offset + limit - 1).execute()

        messages = response.data
        for msg in messages:
            user = msg.get('users') or {}
            avatar_path = user.get('avatar_url')
            if avatar_path and not avatar_path.startswith('http'):
                user['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)
                msg['users'] = user

            file_path = msg.get('file_url')
            if file_path and not file_path.startswith('http'):
                msg['file_url'] = supabase.storage.from_('attachments').get_public_url(file_path)

        return jsonify(messages)
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Gui message theo room id
@room_messages_bp.route('/rooms/<room_id>/messages', methods=['POST'])
@token_required
def send_room_message(current_user_id, room_id):
    try:
        # Check if user is member
        member_check = supabase.table('room_members').select('*').eq('room_id', room_id).eq('user_id', current_user_id).execute()
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403
        
        data = request.get_json()
        if not data.get('content'):
            return jsonify({'error': 'Message content is required'}), 400
        
        message_data = {
            'room_id': room_id,
            'sender_id': current_user_id,
            'content': data['content']
        }
        
        response = supabase.table('room_messages').insert(message_data).execute()
        
        # Get message with user info
        message_id = response.data[0]['message_id']
        full_message = supabase.table('room_messages').select('*, users(user_name, full_name, avatar_url)').eq('message_id', message_id).execute()

        message = full_message.data[0]
        user = message.get('users') or {}
        avatar_path = user.get('avatar_url')
        if avatar_path and not avatar_path.startswith('http'):
            user['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)
            message['users'] = user

        file_path = message.get('file_url')
        if file_path and not file_path.startswith('http'):
            message['file_url'] = supabase.storage.from_('attachments').get_public_url(file_path)

        return jsonify(message), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 500

# Xoa message theo room_id
@room_messages_bp.route('/rooms/<room_id>/messages/<int:message_id>', methods=['DELETE'])
@token_required
def delete_room_message(current_user_id, room_id, message_id):
    try:
        # Check if user is sender or room owner
        message_check = supabase.table('room_messages').select('sender_id').eq('message_id', message_id).eq('room_id', room_id).execute()
        if not message_check.data:
            return jsonify({'error': 'Message not found'}), 404
        
        room_check = supabase.table('rooms').select('owner_id').eq('room_id', room_id).execute()
        
        if message_check.data[0]['sender_id'] != current_user_id and room_check.data[0]['owner_id'] != current_user_id:
            return jsonify({'error': 'Access denied'}), 403
        
        supabase.table('room_messages').delete().eq('message_id', message_id).execute()
        return jsonify({'message': 'Message deleted successfully'})
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    
@room_messages_bp.route('/rooms/<room_id>/messages/latest', methods=['GET'])
@token_required
def get_latest_message(current_user_id, room_id):
    """Return the most recent message of a room"""
    try:
        # Check membership
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

        response = (
            supabase
            .table('room_messages')
            .select('*, users(user_name, full_name, avatar_url)')
            .eq('room_id', room_id)
            .order('sent_at', desc=True)
            .limit(1)
            .execute()
        )
        if not response.data:
            return jsonify({})
        message = response.data[0]
        user = message.get('users') or {}
        avatar_path = user.get('avatar_url')
        if avatar_path and not avatar_path.startswith('http'):
            user['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)
            message['users'] = user

        file_path = message.get('file_url')
        if file_path and not file_path.startswith('http'):
            message['file_url'] = supabase.storage.from_('attachments').get_public_url(file_path)

        return jsonify(message)
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@room_messages_bp.route('/rooms/<room_id>/messages/attachments', methods=['POST'])
@token_required
def send_room_attachment(current_user_id, room_id):
    """Upload an attachment and create a message referencing it"""
    try:
        member_check = supabase.table('room_members').select('*').eq('room_id', room_id).eq('user_id', current_user_id).execute()
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403

        if not request.content_type or not request.content_type.startswith('multipart/form-data'):
            return jsonify({'error': 'Invalid content type'}), 400

        file = request.files.get('file')
        msg_type = request.form.get('type', 'file')
        if not file:
            return jsonify({'error': 'File is required'}), 400

        ext = os.path.splitext(file.filename)[1] or ''
        unique_name = f"room_{room_id}_{uuid.uuid4().hex}{ext}"
        file_bytes = file.read()
        supabase.storage.from_('attachments').upload(unique_name, file_bytes, {
            'content-type': file.mimetype
        })

        message_data = {
            'room_id': room_id,
            'sender_id': current_user_id,
            'file_url': unique_name,
            'file_name': file.filename,
            'file_size': len(file_bytes),
            'message_type': msg_type
        }

        response = supabase.table('room_messages').insert(message_data).execute()

        message_id = response.data[0]['message_id']
        full_message = supabase.table('room_messages').select('*, users(user_name, full_name, avatar_url)').eq('message_id', message_id).execute()
        message = full_message.data[0]
        user = message.get('users') or {}
        avatar_path = user.get('avatar_url')
        if avatar_path and not avatar_path.startswith('http'):
            user['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)
            message['users'] = user

        file_path = message.get('file_url')
        if file_path and not file_path.startswith('http'):
            message['file_url'] = supabase.storage.from_('attachments').get_public_url(file_path)

        return jsonify(message), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 500