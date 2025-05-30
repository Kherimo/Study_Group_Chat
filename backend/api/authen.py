
from flask import Flask, request, jsonify
import jwt
from datetime import datetime, timedelta
from functools import wraps
import bcrypt
from flask import Blueprint, request, jsonify
from supabase_client import supabase, JWT_SECRET_KEY, JWT_REFRESH_SECRET_KEY
from api.autho import generate_access_token, generate_refresh_token

authen_bp = Blueprint("authen", __name__, url_prefix="/authen")

# Helper functions
def generate_password_hash(password):
    return bcrypt.hashpw(password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

def check_password(password, hashed):
    return bcrypt.checkpw(password.encode('utf-8'), hashed.encode('utf-8'))

@authen_bp.route('/register', methods=['POST'])
def register():
    try:
        data = request.get_json()
        required_fields = ['user_name', 'email', 'phone_number', 'password']
        
        if not all(field in data for field in required_fields):
            return jsonify({'error': 'Missing required fields'}), 400
        
        # Hash password
        password_hash = generate_password_hash(data['password'])
        
        user_data = {
            'user_name': data['user_name'],
            'full_name': data.get('full_name'),
            'email': data['email'],
            'phone_number': data['phone_number'],
            'password_hash': password_hash,
            'avatar_url': data.get('avatar_url')
        }
        
        response = supabase.table('users').insert(user_data).execute()
        user = response.data[0]
        
        # Generate tokens
        access_token = generate_access_token(user['user_id'])
        refresh_token = generate_refresh_token(user['user_id'])
        
        return jsonify({
            'message': 'User registered successfully',
            'user': {k: v for k, v in user.items() if k != 'password_hash'},
            'access_token': access_token,
            'refresh_token': refresh_token
        }), 201
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@authen_bp.route('/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        
        if not data.get('user_name') or not data.get('password'):
            return jsonify({'error': 'Username and password required'}), 400
        
        # Get user by user_name
        response = supabase.table('users').select('*').eq('user_name', data['user_name']).execute()
        
        if not response.data:
            return jsonify({'error': 'Invalid credentials'}), 401
        
        user = response.data[0]
        
        # Check password
        if not check_password(data['password'], user['password_hash']):
            return jsonify({'error': 'Invalid credentials'}), 401
        
        # Generate tokens
        access_token = generate_access_token(user['user_id'])
        refresh_token = generate_refresh_token(user['user_id'])
        
        return jsonify({
            'message': 'Login successful',
            'user': {k: v for k, v in user.items() if k != 'password_hash'},
            'access_token': access_token,
            'refresh_token': refresh_token
        })
        
    except Exception as e:
        return jsonify({'error': str(e)}), 500
