from flask import Blueprint, request, jsonify
from supabase_client import supabase 
from decorators import token_required
from api.authen import generate_password_hash, check_password
import uuid
import os


users_bp = Blueprint("users", __name__, url_prefix="/api")



@users_bp.route('/users/me', methods=['GET'])
@token_required
def get_current_user(current_user_id):
    try:
        response = supabase.table('users').select('user_id, user_name, full_name, email, phone_number, avatar_url, created_at').eq('user_id', current_user_id).execute()
        user = response.data[0]
        avatar_path = user.get('avatar_url')
        if avatar_path and not avatar_path.startswith('htp'):
            user['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)
        return jsonify(user)
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@users_bp.route('/users/me', methods=['PUT'])
@token_required
def update_current_user(current_user_id):
    try:
        if request.content_type.startswith('multipart/form-data'):
            data = request.form.to_dict()
            avatar_file = request.files.get('avatar')
        else:
            data = request.get_json() or {}
            avatar_file = None

        allowed_fields = ['full_name', 'email', 'phone_number']
        update_data = {k: v for k, v in data.items() if k in allowed_fields}

        if avatar_file:
            ext = os.path.splitext(avatar_file.filename)[1] or '.jpg'
            unique_name = f"{current_user_id}_{uuid.uuid4().hex}{ext}"
            file_bytes = avatar_file.read()
            supabase.storage.from_('avatars').upload(unique_name, file_bytes, {
                "content-type": avatar_file.mimetype
            })
            update_data['avatar_url'] = unique_name

        if not update_data:
            return jsonify({'error': 'No valid fields to update'}), 400

        response = supabase.table('users').update(update_data).eq('user_id', current_user_id).execute()
        if not response.data:
            return jsonify({'error': 'User not found or no changes made'}), 404

        user = response.data[0]
        avatar_path = user.get('avatar_url')
        if avatar_path and not avatar_path.startswith('http'):
            user['avatar_url'] = supabase.storage.from_('avatars').get_public_url(avatar_path)

        return jsonify(user)
    except Exception as e:
        return jsonify({'error': str(e)}), 500



@users_bp.route('/users/me/password', methods=['PUT'])
@token_required
def change_password(current_user_id):
    try:
        data = request.get_json()
        
        if not data.get('current_password') or not data.get('new_password'):
            return jsonify({'error': 'Current password and new password required'}), 400
        
        # Get current user
        response = supabase.table('users').select('password_hash').eq('user_id', current_user_id).execute()
        user = response.data[0]
        
        # Verify current password
        if not check_password(data['current_password'], user['password_hash']):
            return jsonify({'error': 'Current password is incorrect'}), 400
        
        # Update password
        new_password_hash = generate_password_hash(data['new_password'])
        supabase.table('users').update({'password_hash': new_password_hash}).eq('user_id', current_user_id).execute()
        
        return jsonify({'message': 'Password updated successfully'})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

