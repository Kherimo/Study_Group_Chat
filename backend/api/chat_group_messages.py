from flask import Blueprint, request, jsonify
from supabase_client import supabase 
from decorators import token_required
from datetime import datetime


chat_group_messages_bp = Blueprint("chat_group_messages", __name__, url_prefix="/api")

@chat_group_messages_bp.route('/chat-groups/<group_id>/messages', methods=['GET'])
@token_required
def get_chat_group_messages(current_user_id, group_id):
    try:
        # Check if user is member
        member_check = supabase.table('chat_group_members').select('*').eq('group_id', group_id).eq('user_id', current_user_id).execute()
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403

        page = int(request.args.get('page', 1))
        limit = int(request.args.get('limit', 50))
        offset = (page - 1) * limit

        response = supabase.table('chat_group_messages')\
            .select('*, users(user_name, full_name, avatar_url)')\
            .eq('group_id', group_id)\
            .order('sent_at', desc=True)\
            .range(offset, offset + limit - 1).execute()

        return jsonify(response.data)
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@chat_group_messages_bp.route('/chat-groups/<group_id>/messages', methods=['POST'])
@token_required
def send_chat_group_message(current_user_id, group_id):
    try:
        # Check if user is member
        member_check = supabase.table('chat_group_members').select('*').eq('group_id', group_id).eq('user_id', current_user_id).execute()
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403
        
        data = request.get_json()
        if not data.get('content_text') and not data.get('content_file_url'):
            return jsonify({'error': 'Message content or file is required'}), 400
        
        message_data = {
            'group_id': group_id,
            'sender_id': current_user_id,
            'content_text': data.get('content_text'),
            'content_file_url': data.get('content_file_url')
        }
        
        response = supabase.table('chat_group_messages').insert(message_data).execute()
        
        # Get message with user info
        message_id = response.data[0]['message_id']
        full_message = supabase.table('chat_group_messages').select('*, users(user_name, full_name, avatar_url)').eq('message_id', message_id).execute()
        
        return jsonify(full_message.data[0]), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 500
@chat_group_messages_bp.route('/chat-groups/<group_id>/messages/<int:message_id>', methods=['DELETE'])
@token_required
def delete_chat_group_message(current_user_id, group_id, message_id):
    try:
        # Kiểm tra user có phải thành viên nhóm không
        member_check = supabase.table('chat_group_members') \
            .select('user_id') \
            .eq('group_id', group_id) \
            .eq('user_id', current_user_id) \
            .execute()
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403

        # Kiểm tra thông tin message và người gửi
        message_check = supabase.table('chat_group_messages') \
            .select('sender_id, group_id') \
            .eq('message_id', message_id) \
            .execute()
        if not message_check.data:
            return jsonify({'error': 'Message not found'}), 404

        message = message_check.data[0]

        # Kiểm tra người tạo nhóm
        group_check = supabase.table('chat_groups') \
            .select('created_by') \
            .eq('group_id', group_id) \
            .execute()
        if not group_check.data:
            return jsonify({'error': 'Group not found'}), 404

        group_creator = group_check.data[0]['created_by']
        if message['sender_id'] != current_user_id and group_creator != current_user_id:
            return jsonify({'error': 'Access denied'}), 403

        # Xóa tin nhắn
        supabase.table('chat_group_messages').delete().eq('message_id', message_id).execute()
        return jsonify({'message': 'Message deleted successfully'})
    except Exception as e:
        return jsonify({'error': str(e)}), 500
