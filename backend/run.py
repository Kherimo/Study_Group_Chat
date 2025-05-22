# run.py
from flask import Flask
from flask_cors import CORS
from app.api.auth_routes import auth_bp
from app.api.auth_google import auth_google_bp

app = Flask(__name__)
CORS(app)
app.register_blueprint(auth_bp)
app.register_blueprint(auth_google_bp)


if __name__ == '__main__':
    app.run(debug=True)

# cloudflared tunnel --url http://localhost:5000

