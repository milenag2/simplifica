Text file: reports.py
Latest content with line numbers:
1	# Rotas para relatórios e cálculos financeiros
2	from flask import Blueprint, request, jsonify
3	from datetime import datetime, timedelta
4	from sqlalchemy import func, extract
5	from src.models.user import db
6	from src.models.transaction import Transaction
7	from src.models.category import Category
8	
9	# Criando o blueprint para as rotas de relatórios
10	reports_bp = Blueprint('reports', __name__)
11	
12	@reports_bp.route('/balance/monthly', methods=['GET'])
13	def get_monthly_balance():
14	    """
15	    Endpoint para calcular o saldo mensal de um usuário.
16	    
17	    Query Parameters:
18	        user_id: ID do usuário (obrigatório)
19	        year: Ano (opcional, default: ano atual)
20	        month: Mês (opcional, default: mês atual)
21	    
22	    Returns:
23	        JSON: Saldo mensal (receitas - despesas)
24	    """
25	    try:
26	        # Obtém os parâmetros da query
27	        user_id = request.args.get('user_id')
28	        year = request.args.get('year', datetime.now().year)
29	        month = request.args.get('month', datetime.now().month)
30	        
31	        if not user_id:
32	            return jsonify({'error': 'user_id é obrigatório'}), 400
33	        
34	        # Converte para inteiros
35	        year = int(year)
36	        month = int(month)
37	        
38	        # Calcula o total de receitas do mês
39	        income_total = db.session.query(func.sum(Transaction.amount)).filter(
40	            Transaction.user_id == user_id,
41	            Transaction.type == 'income',
42	            extract('year', Transaction.date) == year,
43	            extract('month', Transaction.date) == month
44	        ).scalar() or 0
45	        
46	        # Calcula o total de despesas do mês
47	        expense_total = db.session.query(func.sum(Transaction.amount)).filter(
48	            Transaction.user_id == user_id,
49	            Transaction.type == 'expense',
50	            extract('year', Transaction.date) == year,
51	            extract('month', Transaction.date) == month
52	        ).scalar() or 0
53	        
54	        # Calcula o saldo (receitas - despesas)
55	        balance = income_total - expense_total
56	        
57	        return jsonify({
58	            'success': True,
59	            'year': year,
60	            'month': month,
61	            'income_total': income_total,
62	            'expense_total': expense_total,
63	            'balance': balance
64	        }), 200
65	        
66	    except Exception as e:
67	        return jsonify({'error': str(e)}), 500
68	
69	@reports_bp.route('/reports/daily', methods=['GET'])
70	def get_daily_report():
71	    """
72	    Endpoint para obter relatório diário de transações.
73	    
74	    Query Parameters:
75	        user_id: ID do usuário (obrigatório)
76	        date: Data específica (opcional, formato: YYYY-MM-DD, default: hoje)
77	    
78	    Returns:
79	        JSON: Relatório diário com transações e totais
80	    """
81	    try:
82	        # Obtém os parâmetros da query
83	        user_id = request.args.get('user_id')
84	        date_str = request.args.get('date')
85	        
86	        if not user_id:
87	            return jsonify({'error': 'user_id é obrigatório'}), 400
88	        
89	        # Define a data (hoje se não especificada)
90	        if date_str:
91	            target_date = datetime.strptime(date_str, '%Y-%m-%d').date()
92	        else:
93	            target_date = datetime.now().date()
94	        
95	        # Busca todas as transações do dia
96	        transactions = Transaction.query.filter(
97	            Transaction.user_id == user_id,
98	            func.date(Transaction.date) == target_date
99	        ).order_by(Transaction.date.desc()).all()
100	        
101	        # Calcula totais
102	        income_total = sum(t.amount for t in transactions if t.type == 'income')
103	        expense_total = sum(t.amount for t in transactions if t.type == 'expense')
104	        
105	        return jsonify({
106	            'success': True,
107	            'date': target_date.isoformat(),
108	            'transactions': [t.to_dict() for t in transactions],
109	            'income_total': income_total,
110	            'expense_total': expense_total,
111	            'balance': income_total - expense_total,
112	            'transaction_count': len(transactions)
113	        }), 200
114	        
115	    except Exception as e:
116	        return jsonify({'error': str(e)}), 500
117	
118	@reports_bp.route('/reports/monthly', methods=['GET'])
119	def get_monthly_report():
120	    """
121	    Endpoint para obter relatório mensal de transações.
122	    
123	    Query Parameters:
124	        user_id: ID do usuário (obrigatório)
125	        year: Ano (opcional, default: ano atual)
126	        month: Mês (opcional, default: mês atual)
127	    
128	    Returns:
129	        JSON: Relatório mensal com transações e totais
130	    """
131	    try:
132	        # Obtém os parâmetros da query
133	        user_id = request.args.get('user_id')
134	        year = request.args.get('year', datetime.now().year)
135	        month = request.args.get('month', datetime.now().month)
136	        
137	        if not user_id:
138	            return jsonify({'error': 'user_id é obrigatório'}), 400
139	        
140	        # Converte para inteiros
141	        year = int(year)
142	        month = int(month)
143	        
144	        # Busca todas as transações do mês
145	        transactions = Transaction.query.filter(
146	            Transaction.user_id == user_id,
147	            extract('year', Transaction.date) == year,
148	            extract('month', Transaction.date) == month
149	        ).order_by(Transaction.date.desc()).all()
150	        
151	        # Calcula totais
152	        income_total = sum(t.amount for t in transactions if t.type == 'income')
153	        expense_total = sum(t.amount for t in transactions if t.type == 'expense')
154	        
155	        return jsonify({
156	            'success': True,
157	            'year': year,
158	            'month': month,
159	            'transactions': [t.to_dict() for t in transactions],
160	            'income_total': income_total,
161	            'expense_total': expense_total,
162	            'balance': income_total - expense_total,
163	            'transaction_count': len(transactions)
164	        }), 200
165	        
166	    except Exception as e:
167	        return jsonify({'error': str(e)}), 500
168	
169	@reports_bp.route('/reports/annual', methods=['GET'])
170	def get_annual_report():
171	    """
172	    Endpoint para obter relatório anual de transações.
173	    
174	    Query Parameters:
175	        user_id: ID do usuário (obrigatório)
176	        year: Ano (opcional, default: ano atual)
177	    
178	    Returns:
179	        JSON: Relatório anual com transações e totais por mês
180	    """
181	    try:
182	        # Obtém os parâmetros da query
183	        user_id = request.args.get('user_id')
184	        year = request.args.get('year', datetime.now().year)
185	        
186	        if not user_id:
187	            return jsonify({'error': 'user_id é obrigatório'}), 400
188	        
189	        # Converte para inteiro
190	        year = int(year)
191	        
192	        # Busca todas as transações do ano
193	        transactions = Transaction.query.filter(
194	            Transaction.user_id == user_id,
195	            extract('year', Transaction.date) == year
196	        ).order_by(Transaction.date.desc()).all()
197	        
198	        # Agrupa por mês
199	        monthly_data = {}
200	        for month in range(1, 13):
201	            monthly_transactions = [t for t in transactions if t.date.month == month]
202	            income_total = sum(t.amount for t in monthly_transactions if t.type == 'income')
203	            expense_total = sum(t.amount for t in monthly_transactions if t.type == 'expense')
204	            
205	            monthly_data[month] = {
206	                'month': month,
207	                'income_total': income_total,
208	                'expense_total': expense_total,
209	                'balance': income_total - expense_total,
210	                'transaction_count': len(monthly_transactions)
211	            }
212	        
213	        # Calcula totais anuais
214	        annual_income = sum(t.amount for t in transactions if t.type == 'income')
215	        annual_expense = sum(t.amount for t in transactions if t.type == 'expense')
216	        
217	        return jsonify({
218	            'success': True,
219	            'year': year,
220	            'monthly_data': monthly_data,
221	            'annual_income': annual_income,
222	            'annual_expense': annual_expense,
223	            'annual_balance': annual_income - annual_expense,
224	            'total_transactions': len(transactions)
225	        }), 200
226	        
227	    except Exception as e:
228	        return jsonify({'error': str(e)}), 500
229	
230	@reports_bp.route('/reports/categories', methods=['GET'])
231	def get_category_report():
232	    """
233	    Endpoint para obter relatório de transações por categoria.
234	    
235	    Query Parameters:
236	        user_id: ID do usuário (obrigatório)
237	        start_date: Data de início (opcional, formato: YYYY-MM-DD)
238	        end_date: Data de fim (opcional, formato: YYYY-MM-DD)
239	        type: Tipo de transação (opcional, 'expense' ou 'income')
240	    
241	    Returns:
242	        JSON: Relatório por categoria com totais
243	    """
244	    try:
245	        # Obtém os parâmetros da query
246	        user_id = request.args.get('user_id')
247	        start_date = request.args.get('start_date')
248	        end_date = request.args.get('end_date')
249	        transaction_type = request.args.get('type')
250	        
251	        if not user_id:
252	            return jsonify({'error': 'user_id é obrigatório'}), 400
253	        
254	        # Inicia a query base
255	        query = db.session.query(
256	            Category.id,
257	            Category.name,
258	            Category.type,
259	            func.sum(Transaction.amount).label('total'),
260	            func.count(Transaction.id).label('count')
261	        ).join(Transaction).filter(Transaction.user_id == user_id)
262	        
263	        # Aplica filtros opcionais
264	        if start_date:
265	            start_date_obj = datetime.strptime(start_date, '%Y-%m-%d')
266	            query = query.filter(Transaction.date >= start_date_obj)
267	        
268	        if end_date:
269	            end_date_obj = datetime.strptime(end_date, '%Y-%m-%d')
270	            query = query.filter(Transaction.date <= end_date_obj)
271	        
272	        if transaction_type:
273	            query = query.filter(Transaction.type == transaction_type)
274	        
275	        # Agrupa por categoria
276	        results = query.group_by(Category.id, Category.name, Category.type).all()
277	        
278	        # Formata os resultados
279	        category_data = []
280	        for result in results:
281	            category_data.append({
282	                'category_id': result.id,
283	                'category_name': result.name,
284	                'category_type': result.type,
285	                'total_amount': float(result.total),
286	                'transaction_count': result.count
287	            })
288	        
289	        return jsonify({
290	            'success': True,
291	            'category_data': category_data,
292	            'filters': {
293	                'start_date': start_date,
294	                'end_date': end_date,
295	                'type': transaction_type
296	            }
297	        }), 200
298	        
299	    except Exception as e:
300	        return jsonify({'error': str(e)}), 500
301	
302	