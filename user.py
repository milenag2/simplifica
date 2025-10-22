Text file: user.py
Latest content with line numbers:
1	from flask_sqlalchemy import SQLAlchemy
2	
3	db = SQLAlchemy()
4	
5	class User(db.Model):
6	    id = db.Column(db.Integer, primary_key=True)
7	    username = db.Column(db.String(80), unique=True, nullable=False)
8	    email = db.Column(db.String(120), unique=True, nullable=False)
9	
10	    def __repr__(self):
11	        return f'<User {self.username}>'
12	
13	    def to_dict(self):
14	        return {
15	            'id': self.id,
16	            'username': self.username,
17	            'email': self.email
18	        }
19	