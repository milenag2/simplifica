Text file: chart-utils.js
Latest content with line numbers:
1	/**
2	 * chart-utils.js - Utilitários para criação e gerenciamento de gráficos
3	 * 
4	 * Este arquivo contém funções auxiliares para criar diferentes tipos de gráficos
5	 * usando a biblioteca Chart.js, incluindo gráficos de pizza, barras e linhas.
6	 * 
7	 * Funcionalidades:
8	 * - Criação de gráficos de categorias (pizza)
9	 * - Gráficos de evolução temporal (linha)
10	 * - Gráficos comparativos (barras)
11	 * - Configurações responsivas
12	 * - Temas e cores personalizadas
13	 */
14	
15	// Configuração global do Chart.js
16	Chart.defaults.font.family = 'Inter, system-ui, -apple-system, sans-serif';
17	Chart.defaults.font.size = 12;
18	Chart.defaults.color = '#4A5568';
19	
20	// Paleta de cores para os gráficos
21	const CHART_COLORS = [
22	    '#667eea', // Azul primário
23	    '#764ba2', // Roxo
24	    '#f093fb', // Rosa
25	    '#4facfe', // Azul claro
26	    '#43e97b', // Verde
27	    '#38ef7d', // Verde claro
28	    '#ffecd2', // Amarelo claro
29	    '#fcb69f', // Laranja claro
30	    '#ff9a9e', // Rosa claro
31	    '#a8edea'  // Azul muito claro
32	];
33	
34	// Cores específicas para receitas e despesas
35	const INCOME_COLOR = '#48BB78';
36	const EXPENSE_COLOR = '#F56565';
37	const BALANCE_COLOR = '#4299E1';
38	
39	/**
40	 * Cria um gráfico de pizza para mostrar distribuição por categorias
41	 * 
42	 * @param {string} canvasId - ID do elemento canvas
43	 * @param {Array} data - Array de objetos com {category, amount, type}
44	 * @param {string} title - Título do gráfico
45	 * @returns {Chart} Instância do gráfico criado
46	 */
47	function createCategoryPieChart(canvasId, data, title = 'Gastos por Categoria') {
48	    const ctx = document.getElementById(canvasId);
49	    if (!ctx) {
50	        console.error(`Canvas com ID '${canvasId}' não encontrado`);
51	        return null;
52	    }
53	
54	    // Processa os dados para o formato do Chart.js
55	    const processedData = processCategoryData(data);
56	    
57	    const config = {
58	        type: 'pie',
59	        data: {
60	            labels: processedData.labels,
61	            datasets: [{
62	                data: processedData.values,
63	                backgroundColor: processedData.colors,
64	                borderColor: '#ffffff',
65	                borderWidth: 2,
66	                hoverOffset: 10
67	            }]
68	        },
69	        options: {
70	            responsive: true,
71	            maintainAspectRatio: false,
72	            plugins: {
73	                title: {
74	                    display: true,
75	                    text: title,
76	                    font: {
77	                        size: 16,
78	                        weight: 'bold'
79	                    },
80	                    padding: 20
81	                },
82	                legend: {
83	                    position: 'bottom',
84	                    labels: {
85	                        padding: 15,
86	                        usePointStyle: true,
87	                        font: {
88	                            size: 11
89	                        }
90	                    }
91	                },
92	                tooltip: {
93	                    callbacks: {
94	                        label: function(context) {
95	                            const label = context.label || '';
96	                            const value = context.parsed || 0;
97	                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
98	                            const percentage = ((value / total) * 100).toFixed(1);
99	                            return `${label}: R$ ${value.toFixed(2)} (${percentage}%)`;
100	                        }
101	                    }
102	                }
103	            },
104	            animation: {
105	                animateRotate: true,
106	                animateScale: true,
107	                duration: 1000
108	            }
109	        }
110	    };
111	
112	    return new Chart(ctx, config);
113	}
114	
115	/**
116	 * Cria um gráfico de barras para comparar receitas e despesas
117	 * 
118	 * @param {string} canvasId - ID do elemento canvas
119	 * @param {Array} data - Array de objetos com dados mensais
120	 * @param {string} title - Título do gráfico
121	 * @returns {Chart} Instância do gráfico criado
122	 */
123	function createIncomeExpenseChart(canvasId, data, title = 'Receitas vs Despesas') {
124	    const ctx = document.getElementById(canvasId);
125	    if (!ctx) {
126	        console.error(`Canvas com ID '${canvasId}' não encontrado`);
127	        return null;
128	    }
129	
130	    // Processa os dados para o formato do Chart.js
131	    const processedData = processIncomeExpenseData(data);
132	    
133	    const config = {
134	        type: 'bar',
135	        data: {
136	            labels: processedData.labels,
137	            datasets: [
138	                {
139	                    label: 'Receitas',
140	                    data: processedData.income,
141	                    backgroundColor: INCOME_COLOR,
142	                    borderColor: INCOME_COLOR,
143	                    borderWidth: 1,
144	                    borderRadius: 4,
145	                    borderSkipped: false
146	                },
147	                {
148	                    label: 'Despesas',
149	                    data: processedData.expenses,
150	                    backgroundColor: EXPENSE_COLOR,
151	                    borderColor: EXPENSE_COLOR,
152	                    borderWidth: 1,
153	                    borderRadius: 4,
154	                    borderSkipped: false
155	                }
156	            ]
157	        },
158	        options: {
159	            responsive: true,
160	            maintainAspectRatio: false,
161	            plugins: {
162	                title: {
163	                    display: true,
164	                    text: title,
165	                    font: {
166	                        size: 16,
167	                        weight: 'bold'
168	                    },
169	                    padding: 20
170	                },
171	                legend: {
172	                    position: 'top',
173	                    labels: {
174	                        padding: 15,
175	                        usePointStyle: true
176	                    }
177	                },
178	                tooltip: {
179	                    callbacks: {
180	                        label: function(context) {
181	                            const label = context.dataset.label || '';
182	                            const value = context.parsed.y || 0;
183	                            return `${label}: R$ ${value.toFixed(2)}`;
184	                        }
185	                    }
186	                }
187	            },
188	            scales: {
189	                x: {
190	                    grid: {
191	                        display: false
192	                    },
193	                    ticks: {
194	                        font: {
195	                            size: 11
196	                        }
197	                    }
198	                },
199	                y: {
200	                    beginAtZero: true,
201	                    grid: {
202	                        color: '#E2E8F0'
203	                    },
204	                    ticks: {
205	                        callback: function(value) {
206	                            return 'R$ ' + value.toFixed(0);
207	                        },
208	                        font: {
209	                            size: 11
210	                        }
211	                    }
212	                }
213	            },
214	            animation: {
215	                duration: 1000,
216	                easing: 'easeInOutQuart'
217	            }
218	        }
219	    };
220	
221	    return new Chart(ctx, config);
222	}
223	
224	/**
225	 * Cria um gráfico de linha para mostrar evolução do saldo ao longo do tempo
226	 * 
227	 * @param {string} canvasId - ID do elemento canvas
228	 * @param {Array} data - Array de objetos com dados temporais
229	 * @param {string} title - Título do gráfico
230	 * @returns {Chart} Instância do gráfico criado
231	 */
232	function createBalanceEvolutionChart(canvasId, data, title = 'Evolução do Saldo') {
233	    const ctx = document.getElementById(canvasId);
234	    if (!ctx) {
235	        console.error(`Canvas com ID '${canvasId}' não encontrado`);
236	        return null;
237	    }
238	
239	    // Processa os dados para o formato do Chart.js
240	    const processedData = processBalanceData(data);
241	    
242	    const config = {
243	        type: 'line',
244	        data: {
245	            labels: processedData.labels,
246	            datasets: [{
247	                label: 'Saldo',
248	                data: processedData.values,
249	                borderColor: BALANCE_COLOR,
250	                backgroundColor: BALANCE_COLOR + '20', // 20% de opacidade
251	                borderWidth: 3,
252	                fill: true,
253	                tension: 0.4,
254	                pointBackgroundColor: BALANCE_COLOR,
255	                pointBorderColor: '#ffffff',
256	                pointBorderWidth: 2,
257	                pointRadius: 5,
258	                pointHoverRadius: 8
259	            }]
260	        },
261	        options: {
262	            responsive: true,
263	            maintainAspectRatio: false,
264	            plugins: {
265	                title: {
266	                    display: true,
267	                    text: title,
268	                    font: {
269	                        size: 16,
270	                        weight: 'bold'
271	                    },
272	                    padding: 20
273	                },
274	                legend: {
275	                    display: false
276	                },
277	                tooltip: {
278	                    callbacks: {
279	                        label: function(context) {
280	                            const value = context.parsed.y || 0;
281	                            return `Saldo: R$ ${value.toFixed(2)}`;
282	                        }
283	                    }
284	                }
285	            },
286	            scales: {
287	                x: {
288	                    grid: {
289	                        display: false
290	                    },
291	                    ticks: {
292	                        font: {
293	                            size: 11
294	                        }
295	                    }
296	                },
297	                y: {
298	                    grid: {
299	                        color: '#E2E8F0'
300	                    },
301	                    ticks: {
302	                        callback: function(value) {
303	                            return 'R$ ' + value.toFixed(0);
304	                        },
305	                        font: {
306	                            size: 11
307	                        }
308	                    }
309	                }
310	            },
311	            animation: {
312	                duration: 1500,
313	                easing: 'easeInOutQuart'
314	            }
315	        }
316	    };
317	
318	    return new Chart(ctx, config);
319	}
320	
321	/**
322	 * Processa dados de categorias para o formato do Chart.js
323	 * 
324	 * @param {Array} data - Dados brutos das categorias
325	(Content truncated due to size limit. Use page ranges or line ranges to read remaining content)