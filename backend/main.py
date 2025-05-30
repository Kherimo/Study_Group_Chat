from flask import Flask, request, jsonify
from flask_cors import CORS
from api.authen import authen_bp
from api.autho import autho_bp
from api.rooms import rooms_bp
from api.users import users_bp


app = Flask(__name__)
CORS(app)
app.register_blueprint(autho_bp)  # gắn blueprint vào app
app.register_blueprint(authen_bp)  
app.register_blueprint(rooms_bp)  
app.register_blueprint(users_bp)  

if __name__ == '__main__':
    app.run(debug=True)

# cloudflared tunnel --url http://localhost:5000

