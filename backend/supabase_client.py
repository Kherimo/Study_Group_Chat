from supabase import create_client, Client
import os
from dotenv import load_dotenv
from datetime import datetime, timedelta
from functools import wraps



load_dotenv()

# Configuration
SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_ANON_KEY")
JWT_SECRET_KEY = os.getenv("JWT_SECRET_KEY")
JWT_REFRESH_SECRET_KEY = os.getenv("JWT_SECRET_KEY")

supabase: Client = create_client(SUPABASE_URL, SUPABASE_KEY)
