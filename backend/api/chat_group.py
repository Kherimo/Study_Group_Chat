from flask import Blueprint, request, jsonify
from supabase_client import supabase 
from decorators import token_required
from datetime import datetime
import secrets
import string

chat_group_bp = Blueprint("chat_group", __name__, url_prefix="/api")


def generate_code(length=8):
    return ''.join(secrets.choice(string.ascii_uppercase + string.digits) for _ in range(length))

@chat_group_bp.route('/chat-groups', methods=['GET'])
@token_required
def get_chat_groups(current_user_id):
    try:
        response = supabase.table('chat_group_members').select(
            '''
            group_id,
            chat_groups(
                *,
                users!chat_groups_created_by_fkey(user_name, full_name)
            )
            '''
        ).eq('user_id', current_user_id).execute()

        groups = [item['chat_groups'] for item in response.data]
        return jsonify(groups)
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@chat_group_bp.route('/chat-groups', methods=['POST'])
@token_required
def create_chat_group(current_user_id):
    try:
        data = request.get_json()
        
        if not data.get('group_name'):
            return jsonify({'error': 'Group name is required'}), 400
        
        group_chat_code = generate_code(10)
        group_data = {
            'group_name': data['group_name'],
            'description': data.get('description'),
            'created_by': current_user_id,
            'group_chat_code': group_chat_code
        }
        
        response = supabase.table('chat_groups').insert(group_data).execute()
        group = response.data[0]
        
        # Add creator as member
        supabase.table('chat_group_members').insert({
            'group_id': group['group_id'],
            'user_id': current_user_id
        }).execute()
        
        return jsonify(group), 201
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@chat_group_bp.route('/chat-groups/<group_id>', methods=['GET'])
@token_required
def get_chat_group(current_user_id, group_id):
    try:
        # Kiểm tra xem user có phải thành viên của group không
        member_check = supabase.table('chat_group_members')\
            .select('*')\
            .eq('group_id', group_id)\
            .eq('user_id', current_user_id)\
            .execute()
        
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403
        
        # Lấy thông tin group + thông tin người tạo group (users)
        response = supabase.table('chat_groups')\
            .select('*, users!chat_groups_created_by_fkey(user_name, full_name)')\
            .eq('group_id', group_id)\
            .execute()
        
        if not response.data:
            return jsonify({'error': 'Group not found'}), 404
        
        return jsonify(response.data[0])
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@chat_group_bp.route('/chat-groups/<group_id>', methods=['DELETE'])
@token_required
def delete_chat_group(current_user_id, group_id):
    try:
        # Kiểm tra người dùng có phải là người tạo nhóm không
        group_check = supabase.table('chat_groups')\
            .select('created_by')\
            .eq('group_id', group_id)\
            .execute()
        
        if not group_check.data:
            return jsonify({'error': 'Group not found'}), 404
        
        if group_check.data[0]['created_by'] != current_user_id:
            return jsonify({'error': 'Access denied'}), 403

        # (Tùy chọn) Xóa các liên kết thành viên trước nếu cần
        supabase.table('chat_group_members')\
            .delete()\
            .eq('group_id', group_id)\
            .execute()

        # (Tùy chọn) Xóa các tin nhắn trong nhóm
        supabase.table('chat_group_messages')\
            .delete()\
            .eq('group_id', group_id)\
            .execute()

        # Xóa chính nhóm
        supabase.table('chat_groups')\
            .delete()\
            .eq('group_id', group_id)\
            .execute()

        return jsonify({'message': 'Group deleted successfully'})
    
    except Exception as e:
        return jsonify({'error': str(e)}), 500


@chat_group_bp.route('/chat-groups/join', methods=['POST'])
@token_required
def join_chat_group_by_code(current_user_id):
    try:
        data = request.get_json()
        group_chat_code = data.get('group_chat_code')
        
        if not group_chat_code:
            return jsonify({'error': 'Group chat code is required'}), 400
        
        # Find group by code
        group_response = supabase.table('chat_groups').select('*').eq('group_chat_code', group_chat_code).execute()
        if not group_response.data:
            return jsonify({'error': 'Invalid group chat code'}), 404
        
        group = group_response.data[0]
        
        # Check if already member
        member_check = supabase.table('chat_group_members').select('*').eq('group_id', group['group_id']).eq('user_id', current_user_id).execute()
        if member_check.data:
            return jsonify({'error': 'Already a member of this group'}), 400
        
        # Add as member
        supabase.table('chat_group_members').insert({
            'group_id': group['group_id'],
            'user_id': current_user_id
        }).execute()
        
        return jsonify({'message': 'Joined group successfully', 'group': group})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@chat_group_bp.route('/chat-groups/<group_id>/leave', methods=['POST'])
@token_required
def leave_chat_group(current_user_id, group_id):
    try:
        # Kiểm tra nếu là creator thì không được rời nhóm
        group_check = supabase.table('chat_groups')\
            .select('created_by')\
            .eq('group_id', group_id)\
            .execute()
        
        if group_check.data and group_check.data[0]['created_by'] == current_user_id:
            return jsonify({'error': 'Group creator cannot leave the group'}), 400

        # Kiểm tra người dùng có phải thành viên không
        member_check = supabase.table('chat_group_members')\
            .select('*')\
            .eq('group_id', group_id)\
            .eq('user_id', current_user_id)\
            .execute()

        if not member_check.data:
            return jsonify({'error': 'Not a member of this group'}), 404
        
        # Xoá người dùng khỏi nhóm
        supabase.table('chat_group_members')\
            .delete()\
            .eq('group_id', group_id)\
            .eq('user_id', current_user_id)\
            .execute()

        return jsonify({'message': 'Left group successfully'})

    except Exception as e:
        return jsonify({'error': str(e)}), 500


@chat_group_bp.route('/chat-groups/<group_id>/members', methods=['GET'])
@token_required
def get_chat_group_members(current_user_id, group_id):
    try:
        # Check if user is member
        member_check = supabase.table('chat_group_members').select('*').eq('group_id', group_id).eq('user_id', current_user_id).execute()
        if not member_check.data:
            return jsonify({'error': 'Access denied'}), 403

        response = supabase.table('chat_group_members')\
            .select('*, users(user_id, user_name, full_name, avatar_url)')\
            .eq('group_id', group_id).execute()

        return jsonify(response.data)
    except Exception as e:
        return jsonify({'error': str(e)}), 500
