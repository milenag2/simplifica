Text file: category.py
Latest content with line numbers:
1	# Modelo para categorias de gastos e receitas
2	from src.models.user import db
3	
4	class Category(db.Model):
5	    """
6	    Modelo para representar categorias de transações (gastos e receitas).
7	    
8	    Atributos:
9	        id: Identificador único da categoria
10	        user_id: Referência ao usuário proprietário da categoria
11	        name: Nome da categoria (ex: Alimentação, Transporte, Salário)
12	        type: Tipo da categoria ('expense' para despesa, 'income' para receita)
13	    """
14	    id = db.Column(db.Integer, primary_key=True)
15	    user_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
16	    name = db.Column(db.String(100), nullable=False)
17	    type = db.Column(db.String(20), nullable=False)  # 'expense' ou 'income'
18	    
19	    # Relacionamento com o usuário
20	    user = db.relationship('User', backref=db.backref('categories', lazy=True))
21	
22	    def __repr__(self):
23	        return f'<Category {self.name} ({self.type})>'
24	
25	    def to_dict(self):
26	        """
27	        Converte o objeto Category para um dicionário para serialização JSON.
28	        
29	        Returns:
30	            dict: Dicionário com os dados da categoria
31	        """
32	        return {
33	            'id': self.id,
34	            'user_id': self.user_id,
35	            'name': self.name,
36	            'type': self.type
37	        }
38	
39	