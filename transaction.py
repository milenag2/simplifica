Text file: transaction.py
Latest content with line numbers:
1	# Modelo para transações (gastos e receitas)
2	from datetime import datetime
3	from src.models.user import db
4	
5	class Transaction(db.Model):
6	    """
7	    Modelo para representar transações financeiras (gastos e receitas).
8	    
9	    Atributos:
10	        id: Identificador único da transação
11	        user_id: Referência ao usuário que registrou a transação
12	        category_id: Referência à categoria da transação
13	        description: Descrição da transação
14	        amount: Valor da transação (positivo para receita, negativo para despesa)
15	        type: Tipo da transação ('expense' para despesa, 'income' para receita)
16	        date: Data da transação
17	        is_recurring: Indica se é uma transação recorrente
18	        recurring_frequency: Frequência da recorrência (daily, weekly, monthly, yearly)
19	        recurring_end_date: Data de término da recorrência
20	    """
21	    id = db.Column(db.Integer, primary_key=True)
22	    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
23	    category_id = db.Column(db.Integer, db.ForeignKey('category.id'), nullable=False)
24	    description = db.Column(db.String(200), nullable=False)
25	    amount = db.Column(db.Float, nullable=False)
26	    type = db.Column(db.String(20), nullable=False)  # 'expense' ou 'income'
27	    date = db.Column(db.DateTime, nullable=False, default=datetime.utcnow)
28	    is_recurring = db.Column(db.Boolean, default=False)
29	    recurring_frequency = db.Column(db.String(20))  # 'daily', 'weekly', 'monthly', 'yearly'
30	    recurring_end_date = db.Column(db.DateTime)
31	    
32	    # Relacionamentos
33	    user = db.relationship('User', backref=db.backref('transactions', lazy=True))
34	    category = db.relationship('Category', backref=db.backref('transactions', lazy=True))
35	
36	    def __repr__(self):
37	        return f'<Transaction {self.description}: {self.amount} ({self.type})>'
38	
39	    def to_dict(self):
40	        """
41	        Converte o objeto Transaction para um dicionário para serialização JSON.
42	        
43	        Returns:
44	            dict: Dicionário com os dados da transação
45	        """
46	        return {
47	            'id': self.id,
48	            'user_id': self.user_id,
49	            'category_id': self.category_id,
50	            'description': self.description,
51	            'amount': self.amount,
52	            'type': self.type,
53	            'date': self.date.isoformat() if self.date else None,
54	            'is_recurring': self.is_recurring,
55	            'recurring_frequency': self.recurring_frequency,
56	            'recurring_end_date': self.recurring_end_date.isoformat() if self.recurring_end_date else None,
57	            'category_name': self.category.name if self.category else None
58	        }
59	
60	