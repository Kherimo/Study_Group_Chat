# app/models/user_model.py
from typing import Optional
from dataclasses import dataclass

@dataclass
class User:
    id: Optional[str]
    email: str
    password: str
    full_name: Optional[str] = ""
