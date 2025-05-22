# app/services/auth_service.py
import bcrypt
import jwt
import datetime
from app.database import supabase
from app.config import JWT_SECRET_KEY
from app.models.user_model import User

def register_user(email, password, full_name=""):
    # Kiểm tra email tồn tại
    existing = supabase.table("users").select("id").eq("email", email).execute()
    if existing.data:
        return {"error": "Email đã tồn tại"}, 400

    # Hash mật khẩu
    hashed_pw = bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode()

    # Lưu vào DB
    user_data = {
        "email": email,
        "password": hashed_pw,
        "full_name": full_name
    }
    supabase.table("users").insert(user_data).execute()
    return {"message": "Đăng ký thành công"}, 201

def login_user(email, password):
    result = supabase.table("users").select("*").eq("email", email).execute()
    if not result.data:
        return {"error": "Email không tồn tại"}, 404

    user = result.data[0]
    if not bcrypt.checkpw(password.encode(), user["password"].encode()):
        return {"error": "Sai mật khẩu"}, 401

    # Tạo JWT token
    token = jwt.encode({
        "user_id": user["id"],
        "exp": datetime.datetime.utcnow() + datetime.timedelta(days=7)
    }, JWT_SECRET_KEY, algorithm="HS256")

    return {
        "token": token,
        "user": {
            "email": user["email"],
            "full_name": user["full_name"]
        }
    }, 200
