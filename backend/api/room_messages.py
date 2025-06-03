from flask import Blueprint, request, jsonify
from supabase_client import supabase 
from decorators import token_required
from datetime import datetime
import secrets
import string

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
        
        response = supabase.table('room_messages').select('*, users(user_name, full_name, avatar_url)').eq('room_id', room_id).order('sent_at', desc=True).range(offset, offset + limit - 1).execute()
        return jsonify(response.data)
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
        
        return jsonify(full_message.data[0]), 201
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