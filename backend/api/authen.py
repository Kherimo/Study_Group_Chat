
from flask import Flask, request, jsonify
import jwt
from datetime import datetime, timedelta
from functools import wraps
import bcrypt
from flask import Blueprint, request, jsonify
import logging
from supabase_client import supabase, JWT_SECRET_KEY, JWT_REFRESH_SECRET_KEY
from api.autho import generate_access_token, generate_refresh_token

# Configure logging
logger = logging.getLogger(__name__)

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
        logger.debug('Register request data: %s', data)
        
        if not data:
            return jsonify({'error': 'No data provided'}), 400
            
        required_fields = ['userName', 'email', 'phoneNumber', 'password']
        missing_fields = [field for field in required_fields if field not in data]
        
        if missing_fields:
            return jsonify({
                'error': 'Missing required fields',
                'missing_fields': missing_fields
            }), 400
        
        # Hash password
        password_hash = generate_password_hash(data['password'])
        
        user_data = {
            'user_name': data['userName'],
            'full_name': data.get('fullName'),
            'email': data['email'],
            'phone_number': data['phoneNumber'],
            'password_hash': password_hash,
            'avatar_url': data.get('avatarUrl')
        }
        
        logger.debug('Attempting to insert user: %s', user_data)
        response = supabase.table('users').insert(user_data).execute()
        
        if not response.data:
            return jsonify({'error': 'Failed to create user'}), 500
            
        user = response.data[0]
        logger.debug('User created successfully: %s', user)
        
        # Generate tokens
        access_token = generate_access_token(user['user_id'])
        refresh_token = generate_refresh_token(user['user_id'])
        
        return jsonify({
            'userId': user['user_id'],
            'userName': user['user_name'],
            'fullName': user['full_name'],
            'email': user['email'],
            'phoneNumber': user['phone_number'],
            'avatarUrl': user['avatar_url'],
            'access_token': access_token,
            'refresh_token': refresh_token
        }), 201

        
    except Exception as e:
        logger.error('Registration error: %s', str(e), exc_info=True)
        return jsonify({'error': str(e)}), 500

@authen_bp.route('/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        logger.debug('Login request data: %s', data)
        
        if not data:
            return jsonify({'error': 'No data provided'}), 400
        
        if not data.get('userName') or not data.get('password'):
            return jsonify({'error': 'Username and password required'}), 400
        
        # Get user by user_name
        logger.debug('Attempting to find user: %s', data['userName'])
        response = supabase.table('users').select('*').eq('user_name', data['userName']).execute()
        
        if not response.data:
            logger.warning('User not found: %s', data['userName'])
            return jsonify({'error': 'Invalid credentials'}), 401
        
        user = response.data[0]
        logger.debug('User found: %s', user)
        
        # Check password
        if not check_password(data['password'], user['password_hash']):
            logger.warning('Invalid password for user: %s', data['userName'])
            return jsonify({'error': 'Invalid credentials'}), 401
        
        # Generate tokens
        access_token = generate_access_token(user['user_id'])
        refresh_token = generate_refresh_token(user['user_id'])
        
        logger.debug('Login successful for user: %s', data['userName'])
        return jsonify({
            'access_token': access_token,
            'refresh_token': refresh_token
        })

        
    except Exception as e:
        logger.error('Login error: %s', str(e), exc_info=True)
        return jsonify({'error': str(e)}), 500
