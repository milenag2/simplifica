Text file: app.js
Latest content with line numbers:
2	 * Aplicação de Gestão de Gastos - JavaScript Principal
3	 * 
4	 * Este arquivo contém toda a lógica frontend da aplicação,
5	 * incluindo navegação, comunicação com a API, manipulação de dados
6	 * e geração de gráficos.
7	 */
8	
9	// Configurações globais
10	const API_BASE_URL = '/api';
11	let currentUser = { id: 1 }; // Simulando usuário logado (ID fixo para demonstração)
12	let categories = [];
13	let transactions = [];
14	let charts = {};
15	
16	// Elementos DOM principais
17	const elements = {
18	    // Navegação
19	    navButtons: document.querySelectorAll('.nav-btn'),
20	    sections: document.querySelectorAll('.section'),
21	    
22	    // Dashboard
23	    monthlyIncome: document.getElementById('monthly-income'),
24	    monthlyExpense: document.getElementById('monthly-expense'),
25	    monthlyBalance: document.getElementById('monthly-balance'),
26	    categoryChart: document.getElementById('categoryChart'),
27	    monthlyChart: document.getElementById('monthlyChart'),
28	    
29	    // Transações
30	    addTransactionBtn: document.getElementById('add-transaction-btn'),
31	    transactionsList: document.getElementById('transactions-list'),
32	    filterType: document.getElementById('filter-type'),
33	    filterCategory: document.getElementById('filter-category'),
34	    filterStartDate: document.getElementById('filter-start-date'),
35	    filterEndDate: document.getElementById('filter-end-date'),
36	    applyFilters: document.getElementById('apply-filters'),
37	    
38	    // Categorias
39	    addCategoryBtn: document.getElementById('add-category-btn'),
40	    categoriesGrid: document.getElementById('categories-grid'),
41	    
42	    // Relatórios
43	    reportType: document.getElementById('report-type'),
44	    reportMonth: document.getElementById('report-month'),
45	    reportYear: document.getElementById('report-year'),
46	    generateReport: document.getElementById('generate-report'),
47	    reportContent: document.getElementById('report-content'),
48	    
49	    // Modais
50	    transactionModal: document.getElementById('transaction-modal'),
51	    categoryModal: document.getElementById('category-modal'),
52	    loading: document.getElementById('loading'),
53	    
54	    // Formulários
55	    transactionForm: document.getElementById('transaction-form'),
56	    categoryForm: document.getElementById('category-form')
57	};
58	
59	/**
60	 * Inicialização da aplicação
61	 */
62	document.addEventListener('DOMContentLoaded', function() {
63	    console.log('Iniciando aplicação de gestão de gastos...');
64	    
65	    // Configura event listeners
66	    setupEventListeners();
67	    
68	    // Carrega dados iniciais
69	    loadInitialData();
70	    
71	    // Define data atual nos campos de data
72	    setDefaultDates();
73	});
74	
75	/**
76	 * Configura todos os event listeners da aplicação
77	 */
78	function setupEventListeners() {
79	    // Navegação entre seções
80	    elements.navButtons.forEach(btn => {
81	        btn.addEventListener('click', () => {
82	            const section = btn.dataset.section;
83	            navigateToSection(section);
84	        });
85	    });
86	    
87	    // Botões de adicionar
88	    elements.addTransactionBtn.addEventListener('click', () => openTransactionModal());
89	    elements.addCategoryBtn.addEventListener('click', () => openCategoryModal());
90	    
91	    // Filtros
92	    elements.applyFilters.addEventListener('click', applyTransactionFilters);
93	    
94	    // Relatórios
95	    elements.generateReport.addEventListener('click', generateReport);
96	    
97	    // Formulários
98	    elements.transactionForm.addEventListener('submit', handleTransactionSubmit);
99	    elements.categoryForm.addEventListener('submit', handleCategorySubmit);
100	    
101	    // Modais
102	    setupModalEventListeners();
103	    
104	    // Checkbox de transação recorrente
105	    const recurringCheckbox = document.getElementById('transaction-recurring');
106	    const recurringOptions = document.getElementById('recurring-options');
107	    
108	    recurringCheckbox.addEventListener('change', function() {
109	        recurringOptions.style.display = this.checked ? 'block' : 'none';
110	    });
111	}
112	
113	/**
114	 * Configura event listeners dos modais
115	 */
116	function setupModalEventListeners() {
117	    // Fechar modais
118	    document.getElementById('close-transaction-modal').addEventListener('click', closeTransactionModal);
119	    document.getElementById('close-category-modal').addEventListener('click', closeCategoryModal);
120	    document.getElementById('cancel-transaction').addEventListener('click', closeTransactionModal);
121	    document.getElementById('cancel-category').addEventListener('click', closeCategoryModal);
122	    
123	    // Fechar modal clicando fora
124	    elements.transactionModal.addEventListener('click', (e) => {
125	        if (e.target === elements.transactionModal) closeTransactionModal();
126	    });
127	    
128	    elements.categoryModal.addEventListener('click', (e) => {
129	        if (e.target === elements.categoryModal) closeCategoryModal();
130	    });
131	}
132	
133	/**
134	 * Navega para uma seção específica
135	 */
136	function navigateToSection(sectionName) {
137	    // Remove classe active de todos os botões e seções
138	    elements.navButtons.forEach(btn => btn.classList.remove('active'));
139	    elements.sections.forEach(section => section.classList.remove('active'));
140	    
141	    // Adiciona classe active ao botão e seção correspondentes
142	    document.querySelector(`[data-section="${sectionName}"]`).classList.add('active');
143	    document.getElementById(sectionName).classList.add('active');
144	    
145	    // Carrega dados específicos da seção
146	    switch(sectionName) {
147	        case 'dashboard':
148	            loadDashboardData();
149	            break;
150	        case 'transactions':
151	            loadTransactions();
152	            break;
153	        case 'categories':
154	            loadCategories();
155	            break;
156	        case 'reports':
157	            // Relatórios são carregados sob demanda
158	            break;
159	    }
160	}
161	
162	/**
163	 * Carrega dados iniciais da aplicação
164	 */
165	async function loadInitialData() {
166	    showLoading(true);
167	    
168	    try {
169	        // Carrega categorias primeiro (necessárias para outros componentes)
170	        await loadCategories();
171	        
172	        // Carrega transações
173	        await loadTransactions();
174	        
175	        // Carrega dados do dashboard
176	        await loadDashboardData();
177	        
178	        console.log('Dados iniciais carregados com sucesso');
179	    } catch (error) {
180	        console.error('Erro ao carregar dados iniciais:', error);
181	        showError('Erro ao carregar dados da aplicação');
182	    } finally {
183	        showLoading(false);
184	    }
185	}
186	
187	/**
188	 * Carrega dados do dashboard
189	 */
190	async function loadDashboardData() {
191	    try {
192	        // Carrega saldo mensal
193	        const balance = await fetchMonthlyBalance();
194	        updateBalanceCards(balance);
195	        // Carrega dados para gráficos
196	        loadDashboardCharts();
197	        
198	    } catch (error) {
199	        console.error('Erro ao carregar dados do dashboard:', error);
200	    }
201	}
202	
203	/**
204	 * Busca o saldo mensal atual
205	 */
206	async function fetchMonthlyBalance() {
207	    const now = new Date();
208	    const response = await fetch(`${API_BASE_URL}/balance/monthly?user_id=${currentUser.id}&year=${now.getFullYear()}&month=${now.getMonth() + 1}`);
209	    
210	    if (!response.ok) {
211	        throw new Error('Erro ao buscar saldo mensal');
212	    }
213	    
214	    return await response.json();
215	}
216	
217	/**
218	 * Atualiza os cards de saldo no dashboard
219	 */
220	function updateBalanceCards(balanceData) {
221	    elements.monthlyIncome.textContent = formatCurrency(balanceData.income_total);
222	    elements.monthlyExpense.textContent = formatCurrency(balanceData.expense_total);
223	    elements.monthlyBalance.textContent = formatCurrency(balanceData.balance);
224	    
225	    // Atualiza cor do saldo baseado no valor
226	    const balanceCard = elements.monthlyBalance.closest('.card');
227	    balanceCard.classList.remove('positive', 'negative');
228	    balanceCard.classList.add(balanceData.balance >= 0 ? 'positive' : 'negative');
229	}
230	
231	/**
232	 * Carrega e renderiza os gráficos
233	 */
234	async function loadCharts() {
235	    try {
236	        // Gráfico de categorias
237	        await loadCategoryChart();
238	        
239	        // Gráfico mensal
240	        await loadMonthlyChart();
241	        
242	    } catch (error) {
243	        console.error('Erro ao carregar gráficos:', error);
244	    }
245	}
246	
247	/**
248	 * Carrega o gráfico de gastos por categoria
249	 */
250	async function loadCategoryChart() {
251	    try {
252	        const response = await fetch(`${API_BASE_URL}/reports/categories?user_id=${currentUser.id}&type=expense`);
253	        const data = await response.json();
254	        
255	        if (data.success && data.category_data.length > 0) {
256	            const labels = data.category_data.map(item => item.category_name);
257	            const values = data.category_data.map(item => Math.abs(item.total_amount));
258	            
259	            // Destrói gráfico anterior se existir
260	            if (charts.categoryChart) {
261	                charts.categoryChart.destroy();
262	            }
263	            
264	            // Cria novo gráfico
265	            charts.categoryChart = new Chart(elements.categoryChart, {
266	                type: 'doughnut',
267	                data: {
268	                    labels: labels,
269	                    datasets: [{
270	                        data: values,
271	                        backgroundColor: [
272	                            '#FF6384',
273	                            '#36A2EB',
274	                            '#FFCE56',
275	                            '#4BC0C0',
276	                            '#9966FF',
277	                            '#FF9F40',
278	                            '#FF6384',
279	                            '#C9CBCF'
280	                        ]
281	                    }]
282	                },
283	                options: {
284	                    responsive: true,
285	                    maintainAspectRatio: false,
286	(Content truncated due to size limit. Use page ranges or line ranges to read remaining content)