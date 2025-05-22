from flask import Blueprint, request, jsonify
import requests
from app.database import supabase
import jwt
import datetime
from app.config import JWT_SECRET_KEY

auth_google_bp = Blueprint("auth_google", __name__, url_prefix="/api/auth")

@auth_google_bp.route("/google", methods=["POST"])
def google_login():
    data = request.get_json()
    access_token = data.get("access_token")

    if not access_token:
        return jsonify({"error": "Thiếu access token"}), 400

    # Gọi Supabase để lấy user info
    res = requests.get("https://ealemtafdzulyyexzlru.supabase.co/auth/v1/user", headers={
        "Authorization": f"Bearer {access_token}"
    })

    if res.status_code != 200:
        return jsonify({"error": "Token không hợp lệ"}), 401

    user = res.json()
    email = user.get("email")
    metadata = user.get("user_metadata", {})

    full_name = metadata.get("full_name") or metadata.get("name") or email
    # avatar = metadata.get("avatar_url") or metadata.get("picture")

    if not email:
        return jsonify({"error": "Không tìm thấy email từ Supabase"}), 400

    # Kiểm tra user đã tồn tại trong bảng `users`
    check = supabase.table("users").select("id").eq("email", email).execute()
    if not check.data:
        # Nếu chưa có, thêm mới vào bảng `users`
        supabase.table("users").insert({
            "email": email,
            "full_name": full_name,
            # "avatar_url": avatar
        }).execute()

    # Tạo token ứng dụng (JWT riêng nếu cần)
    token = jwt.encode({
        "user_id": user["id"],
        "email": email,
        "exp": datetime.datetime.utcnow() + datetime.timedelta(days=7)
    }, JWT_SECRET_KEY, algorithm="HS256")

    return jsonify({
        "token": token,
        "user": {
            "email": email,
            "full_name": full_name,
            # "avatar_url": avatar
        }
    })
