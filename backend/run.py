from flask import Flask
from flask_cors import CORS
from app.api.auth_routes import auth_bp
from app.api.auth_google import auth_google_bp
import os

app = Flask(__name__)
CORS(app)

# Đăng ký các blueprint
app.register_blueprint(auth_bp)
app.register_blueprint(auth_google_bp)

if __name__ == '__main__':
    # Lấy PORT từ biến môi trường (Render sẽ cung cấp biến PORT)
    port = int(os.environ.get("PORT", 5000))
    app.run(host="0.0.0.0", port=port)

# cloudflared tunnel --url http://localhost:5000
