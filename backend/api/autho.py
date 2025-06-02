from flask import Flask, request, jsonify
import jwt
from datetime import datetime, timedelta
from functools import wraps
from flask import Blueprint, request, jsonify
from supabase_client import supabase, JWT_SECRET_KEY, JWT_REFRESH_SECRET_KEY


autho_bp = Blueprint("autho", __name__, url_prefix="/autho")

def generate_access_token(user_id):
    payload = {
        'user_id': user_id,
        'exp': datetime.utcnow() + timedelta(minutes=1),
        'iat': datetime.utcnow()
    }
    return jwt.encode(payload, JWT_SECRET_KEY, algorithm='HS256')

def generate_refresh_token(user_id):
    payload = {
        'user_id': user_id,
        'exp': datetime.utcnow() + timedelta(days=30),
        'iat': datetime.utcnow(),
        'type': 'refresh'
    }
    return jwt.encode(payload, JWT_REFRESH_SECRET_KEY, algorithm='HS256')

@autho_bp.route('/refresh', methods=['POST'])
def refresh_token():
    try:
        data = request.get_json()
        refresh_token = data.get('refresh_token')
        
        if not refresh_token:
            return jsonify({'error': 'Refresh token required'}), 400
        
        try:
            payload = jwt.decode(refresh_token, JWT_REFRESH_SECRET_KEY, algorithms=['HS256'])
            if payload.get('type') != 'refresh':
                return jsonify({'error': 'Invalid token type'}), 401
            
            user_id = payload['user_id']
            new_access_token = generate_access_token(user_id)
            
            return jsonify({
                'access_token': new_access_token
            })
            
        except jwt.ExpiredSignatureError:
            return jsonify({'error': 'Refresh token expired'}), 401
        except jwt.InvalidTokenError:
            return jsonify({'error': 'Invalid refresh token'}), 401
            
    except Exception as e:
        return jsonify({'error': str(e)}), 500