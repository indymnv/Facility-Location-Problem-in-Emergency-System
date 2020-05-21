import ilog.concert.*;
import java.util.*;
import ilog.cplex.*;
import java.io.*;

import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
//Las del jxl
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import  java.lang.Math;
import  java.util.Random;

public class Lf3 {
	public static void main(String[] args)  throws IOException {
		long startTime = System.nanoTime();
		long startTimeInstance;
		long endTimeInstance;
		
		//conjunto de parametros
		double [][] demanda=null;       //demanda i en el periodo t
		double [][] cobertura=null;
		double [][][][] distancia=null; //distancia de i a j en periodo t para vehiculo alfa
		double [][] disponibilidadAmb= null; //Binario: localizar una ambulancia en nodo j periodo t
		double [] capacidadAmb=null; //Capacidad para colocar ambulancias
		int u=1;
		double presupuesto=0;
	
	//conjuntos del problema
		int nInstituciones=0;  
		int nDemandas=0;
		int FlotaA=0;
		int FlotaB=0;
		int FlotaC=0;
		int nPeriodos=0;
		int kMax=0;
		
		int o=0;   //Contador de instancias para los archivos 
		double costo=0;
		System.out.println("Paso barrera 0");
		// Space for parameters lecture
		try {
			Workbook libro=Workbook.getWorkbook(new File("C:/Users/Indy Navarro/eclipse-workspace/LocalizacionFlota3/instancia.xls")); //Archivo a usar
			Sheet hoja=libro.getSheet(0);
			nInstituciones= Integer.parseInt(hoja.getCell(1,3).getContents());
			System.out.println(nInstituciones);
			nDemandas= Integer.parseInt(hoja.getCell(1,4).getContents());
			System.out.println(nDemandas);
		    FlotaA= Integer.parseInt(hoja.getCell(1,5).getContents());
		    System.out.println(FlotaA);
		    FlotaB= Integer.parseInt(hoja.getCell(1,6).getContents());
		    System.out.println(FlotaB);
		    FlotaC= Integer.parseInt(hoja.getCell(1,7).getContents());
		    System.out.println(FlotaC);
			nPeriodos= Integer.parseInt(hoja.getCell(1,2).getContents());
			System.out.println(nPeriodos);
			kMax = Integer.parseInt(hoja.getCell(1,8).getContents());
			System.out.println(kMax);
			
			System.out.println("Paso barrera 1");
			//Creación de matrices
			demanda=new double[nDemandas+1][nPeriodos+1];       //demanda i en el periodo t
			cobertura=new double[nPeriodos][nInstituciones+1]; //un periodo menos
			distancia=new double[nDemandas +1][nDemandas+1][nPeriodos][nInstituciones+1]; //distancia de i a j en periodo t para vehiculo alfa
			//Alerta, verificar si las sumas de estos conjuntos de flotas es correcto
			disponibilidadAmb= new double[nDemandas+1][nPeriodos+1]; //Binario: localizar una ambulancia en nodo j periodo t
			capacidadAmb= new double[nDemandas+1]; //Capacidad para colocar ambulancias
			
			
			System.out.println("Paso barrera 2");
			// Recolección de los datos desde el excel para la construcción de la instancia
			
			for(int t=0; t<= nPeriodos; t++) {
				for (int i=0; i <= nDemandas; i++ ) {
					//demanda[i][t]=Double.parseDouble(hoja.getCell(3+t,22+i).getContents());
					demanda[i][t]=Double.parseDouble(hoja.getCell(3+t,22+i).getContents());
					//demanda[i][t]= (Math.random() * ((50) + 1)) + 50;
							
					//System.out.println(demanda[i][t] +"por "+i  +""+t);
				}
			}
			System.out.println("Paso barrera 2A");
			for (int i=0; i <= nPeriodos-1; i++ ) {
				for(int k=0; k<= nInstituciones; k++) {
					cobertura[i][k]=Double.parseDouble(hoja.getCell(3+i,14+k).getContents());
					//System.out.println(cobertura[i][k] +"por "+i  +""+k);
					//cobertura[i][k]=(Math.random() * ((4000-2000) + 1)) + 2000;
				}
			}
			System.out.println("Paso barrera 2B");
	
			for(int k=0; k <= nInstituciones; k++) {  //nInstituciones por suma de flota
				for (int t=0; t<= nPeriodos-1 ; t++) {
					for(int i=0; i<= nDemandas; i++) {
						for (int j=0; j <= nDemandas; j++ ) {
							//distancia[i][j][t][k]=Double.parseDouble(hoja.getCell(3+i+t*12,37+j+k*12).getContents());	
						distancia[i][j][t][k]=Double.parseDouble(hoja.getCell(3+i+t*33,58+j+k*33).getContents());	
						//System.out.println(distancia[i][j][t][k]+"por " + i+j+t+k);
						//if (t==0 || i==j){
							//distancia[i][j][t][k]=0;
						//}
						//distancia[i][j][t][k]=(Math.random() * ((20000-5000) + 1)) + 500;
						}
					}	
					}
				}
			System.out.println("Paso barrera 2C");
			for(int i=0; i<= nDemandas; i++) {
				for (int t=0; t <= nPeriodos; t++ ) {
					//disponibilidadAmb[i][t]=Double.parseDouble(hoja.getCell(3+i,76+t).getContents());
					disponibilidadAmb[i][t]=Double.parseDouble(hoja.getCell(3+i,166+t).getContents());
					//System.out.println(disponibilidadAmb[i][t]+"por " + i+t);
					//disponibilidadAmb[i][t]=1;
				}
			}
			
			System.out.println("Paso barrera 2D");
			for(int i =0; i <= nDemandas; i++) {
				capacidadAmb[i]= Double.parseDouble(hoja.getCell(2+i,202).getContents());
				//capacidadAmb[i]= Double.parseDouble(hoja.getCell(2+i,88).getContents());
				//System.out.println(capacidadAmb[i] + "por  " + i);
				//capacidadAmb[i]=1;
			}
			
			System.out.println("Paso barrera 3");
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("Falló la lectura de parámetros");
		}
		
	////////////////////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////7///////
		//////////////////////////////////////////////////////////////////////////////////////////
		//solve problem
		while (presupuesto < 4000) {
			startTimeInstance =System.nanoTime();
			System.out.println("fase n°" + u + "presupuesto " + presupuesto); 
			
			
try {
	IloCplex modelo = new IloCplex(); 
	
	
	//variables
	IloIntVar[][] y = new IloIntVar[nDemandas+1][nPeriodos+1]; // 1 si nodo i esta cubierto por 3 inst en t y 0 eoc
	IloIntVar[][][][]x = new IloIntVar [nDemandas+1][nPeriodos +1][nInstituciones+1][FlotaA+FlotaB+FlotaC+3]; //si vehiculo r se posiciona en periodo t en nodo j 0 eoc, REVISAR
	IloIntVar[][][][][]z= new IloIntVar[nDemandas+1][nDemandas+1][nPeriodos+1][nInstituciones+1][FlotaA+FlotaB+FlotaC+3]; //1 si el arco i,j es usado en periodo t por el vehiculo alfa, eoc
	IloIntVar[][][]v = new IloIntVar[nDemandas+1][nPeriodos+1][nInstituciones+1]; //1 si nodo i es cubierto por institucion k y 0 en oc.
	
	System.out.println("Paso barrera 4"); 
	//Variables definidas como Binarias
	
	for(int i=0; i <=nDemandas; i++) {
		for( int j=0; j<= nPeriodos; j++) {
			y[i][j] = modelo.intVar(0,1);
		}
	}
	System.out.println("Paso barrera 5"); 
	for(int i=0; i <=nDemandas; i++) {
		for( int t=0; t<= nPeriodos; t++) {
			for(int k=0; k <= nInstituciones; k++) {
				if(k==0) {
				for(int r=0; r<= FlotaA; r++) {			
					x[i][t][k][r] = modelo.intVar(0,1);
		  }
				}
				if(k==1) {
					for(int r=0; r<= FlotaB; r++) {
						x[i][t][k][r+FlotaA+1] = modelo.intVar(0,1);
					  }	
				}
				if(k==2) {
					for(int r=0; r<= FlotaC; r++) {
						x[i][t][k][r+FlotaA+FlotaB+2] = modelo.intVar(0,1);
					  }	
				}
		}
	}
	}
	
	System.out.println("Paso barrera 6");
	
	for(int i=0; i <=nDemandas; i++) {
		for( int j=0; j<= nDemandas; j++) {
			for(int t=0; t <= nPeriodos; t++) {
				for(int k=0; k <= nInstituciones; k++) {
					if(k==0) {
						for(int r=0; r<=FlotaA; r++) {
							z[i][j][t][k][r] = modelo.intVar(0,1);
						}
					}
					if(k==1) {
						for(int r=0; r<=FlotaB; r++) {
							z[i][j][t][k][r+FlotaA+1] = modelo.intVar(0,1);
						}
					}
					if(k==2) {
						for(int r=0; r<= FlotaC; r++) {
							z[i][j][t][k][r+FlotaA+FlotaB+2] = modelo.intVar(0,1);
						}
					}
			}
		  }
		}
	}
	
	System.out.println("Paso barrera 7");
	
	for(int i=0; i <=nDemandas; i++) {
		for( int k=0; k<= nInstituciones; k++) {
			for(int t=0; t <= nPeriodos; t++) {
			v[i][t][k] = modelo.intVar(0,1);
		  }
		}
	}
	
	System.out.println("Paso barrera 8");
	
	 IloLinearNumExpr objetivo = modelo.linearNumExpr(); 
	 IloLinearNumExpr izq = modelo.linearNumExpr();
	 IloLinearNumExpr der = modelo.linearNumExpr();
	 
	 //Función objetivo y restricciones.
	
		//Función objetivo: Maximizar la demanda cubierta.	 
		 for (int i=0; i<= nDemandas; i++) {  
			 for(int t=0; t <= nPeriodos; t++) { 
			 objetivo.addTerm(demanda[i][t], y[i][t]);
			 
		 }
		}
		 
		 modelo.addMaximize(objetivo); //FO
 		 objetivo.clear();    //Limpiar
 		 
 		System.out.println("Paso barrera 9");
 		
 		 // 6.3.3 Una nodo esta multiplementecubierto si las 3 instituciones lo cubren.
 		 
 		 for(int i=0; i<= nDemandas; i++) {
 			 for (int t=0; t<= nPeriodos; t++) {
 				 for( int k=0; k<= nInstituciones; k++) {
 					 
 					 der.addTerm(1, v[i][t][k]);
 					 izq.addTerm(1, y[i][t]);
 					 modelo.addLe(izq, der);
 		 			 izq.clear();
 		 			 der.clear();
 				 }
 			 }
 			 
 		 }
 		 
 		System.out.println("Paso barrera 10");
 		
 		 // 6.3.4 Un nodo i esta cubierto por la institución k en periodo t, si se posiciona un vehiculo dentro 
 		 //del radio de cobertura R en ese mismo periodo.
 		 
 		for(int i=0; i <= nDemandas; i++) {
 			 for(int t=0; t <= nPeriodos-1; t++) {
 				 for (int k=0; k<= nInstituciones; k++) {
 					 if(k==0) {
 						 for(int r=0; r<= FlotaA; r++) {
 							 for(int j=0; j<=nDemandas; j++) {
 								 if(distancia[i][j][t][k]<=cobertura[t][k]) {   
 									der.addTerm(1, x[j][t][k][r]);
 								 }
 							 }
 						
 						 }
 						izq.addTerm(1, v[i][t][k]);          //Revisar el correcto de esta restriccion
			 				modelo.addLe(izq, der);
					 	    izq.clear();
					 		der.clear();
 					 }
 					if(k==1) {
						 for(int r=0; r<= FlotaB; r++) {
							 for(int j=0; j<=nDemandas; j++) {
								 if(distancia[i][j][t][k]<=cobertura[t][k]) {   
									der.addTerm(1, x[j][t][k][r+FlotaA+1]);
								 }
							 }
								 
						 }
						 izq.addTerm(1, v[i][t][k]);          
				 			modelo.addLe(izq, der);
					 	    izq.clear();
					 		der.clear();	
					 }
 					if(k==2) {
						 for(int r=0; r<= FlotaC; r++) {
							 for(int j=0; j<=nDemandas; j++) {
								 if(distancia[i][j][t][k]<=cobertura[t][k]) {   
									der.addTerm(1,x[j][t][k][r+FlotaA+FlotaB+2]);
								 }
							 }
							
						 }
						 izq.addTerm(1, v[i][t][k]);          
				 			modelo.addLe(izq, der);
					 	    izq.clear();
					 		der.clear();
					 }
 					
 					
 					 }
 					 
 				 }
 			 }
 		 
 		
 		System.out.println("Paso barrera 11");
 		 
 		 // 6.3.5 Flujo origen-destino, para cada vehiculo, se debe considerar los nodos ficticios para esta restriccion
 		
 		for(int k =0; k<= nInstituciones; k++) {
 			if(k==0) {
 				for(int r=0; r<= FlotaA; r++) {
 					for(int j=0; j <= nDemandas; j++) {
 		 				 izq.addTerm(1, z[0][j][0][k][r]);                  
 		 			 }
 		 			 for(int i=0; i<= nDemandas; i++) {
 		 				 der.addTerm(1,z[i][nDemandas][nPeriodos-1][k][r]);
 		 			 }
 		 			 modelo.addEq(izq, der);
 		 			 izq.clear();
 		 			 der.clear();
 				}
 			}
 			if(k==1) {
 				for(int r=0; r<= FlotaB; r++) {
 					for(int j=0; j <= nDemandas; j++) {
 		 				 izq.addTerm(1, z[0][j][0][k][r+FlotaA+1]);                   
 		 			 }
 		 			 for(int i=0; i<= nDemandas; i++) {
 		 				 der.addTerm(1,z[i][nDemandas][nPeriodos-1][k][r+FlotaA+1]);
 		 			 }
 		 			 modelo.addEq(izq, der);
 		 			 izq.clear();
 		 			 der.clear();
 				}
 			}
 			if(k==2) {
 				for(int r=0; r<= FlotaC; r++) {
 					for(int j=0; j <= nDemandas; j++) {
 		 				 izq.addTerm(1, z[0][j][0][k][r+FlotaA+FlotaB+2]);                   
 		 			 }
 		 			 for(int i=0; i<= nDemandas; i++) {
 		 				 der.addTerm(1,z[i][nDemandas][nPeriodos-1][k][r+FlotaA+FlotaB+2]);
 		 			 }
 		 			 modelo.addEq(izq, der);
 		 			 izq.clear();
 		 			 der.clear();
 				}
 			}
 		}
 			
 		 
 		System.out.println("Paso barrera 12");
 		
 		 //6.3.6 Bomberos solo puede decidir su localización inicial y quedarse fijo el resto de los periodos.
 		 
 		for(int t=0; t<= nPeriodos-1; t++) {
 			for (int i = 0; i<= nDemandas; i++) {
 				for(int r=0; r<= FlotaB; r++) {
 					izq.addTerm(1, x[i][t][1][r+FlotaA+1]);
 					der.addTerm(1, x[i][t+1][1][r+FlotaA+1]);
 					modelo.addEq(izq, der);
					izq.clear();
					der.clear();
 				}
 				
 			}
 		}
 		
 		 //6.3.7 Los arcos del nodo de origen son binarios ////////// RESTRICCIÓN SERÍA INNECESARIA--> Evitar duplicaciones 
 		 
 		for(int t=0; t<= nPeriodos; t++) {
 		  for(int k =0; k<= nInstituciones; k++) {
 			  if(k==0) {
 				   for(int r=0; r<=FlotaA; r++) {
 					  
 					   for(int j=0; j<= nDemandas; j++) {
 				 		izq.addTerm(1,x[j][t][k][r]);
 				 		
 					  }
 					modelo.addEq(izq, 1);
 					izq.clear();
 			   }
 				 	
 			 }
 			if(k==1) {
				 for(int r=0; r<=FlotaB; r++) {
					
					for(int j=0; j<= nDemandas; j++) {
						izq.addTerm(1,x[j][t][k][r+FlotaA+1]);
					
				 }
					modelo.addEq(izq, 1);
					izq.clear();
			   }
				 	
			 }
 			if(k==2) {
				 for(int r=0; r<=FlotaC; r++) {
					
					for(int j=0; j<= nDemandas; j++) {
						izq.addTerm(1,x[j][t][k][r+FlotaA+FlotaB+2]);
						
				 }
					modelo.addEq(izq, 1);
					izq.clear();
			   } 	
			 }
 		 }
 		}
 		
 		//System.out.println("Paso barrera 13");
 		 //6.3.8 todo lo que entra de un nodo debe salir por el mismo nodo.
 		for(int t=1; t<= nPeriodos-1; t++) { 
 		   for(int j=0; j <= nDemandas; j++) {
 				for(int k=0; k<=nInstituciones; k++) {
 					if(k==0) {
 						for(int r=0; r<=FlotaA; r++) {
 							for(int i=0; i<=nDemandas; i++) {
 								//if(distancia[i][j][t-1][r]<= kMax) {
 									izq.addTerm(1, z[i][j][t-1][k][r]);
 								//}
 								//if(distancia[i][j][t][r]<= kMax) {
 		 							 der.addTerm(1, z[j][i][t][k][r]);
 		 						 //}
 							}
 							modelo.addEq(izq, der);
 							 izq.clear();
 							 der.clear();
 						}
 						
 					}
 					if(k==1) {
 						for(int r=0; r<=FlotaB; r++) {
 							for(int i=0; i<=nDemandas; i++) {
 								//if(distancia[i][j][t-1][r]<= kMax) {
 									izq.addTerm(1, z[i][j][t-1][k][r+FlotaA+1]);
 								//}
 								//if(distancia[i][j][t][r]<= kMax) {
 		 							 der.addTerm(1, z[j][i][t][k][r+FlotaA+1]);
 		 						// }
 							}
 							modelo.addEq(izq, der);
 							 izq.clear();
 							 der.clear();
 						}
 						
 					}
 					if(k==2) {
 						for(int r=0; r<=FlotaC; r++) {
 							for(int i=0; i<=nDemandas; i++) {
 								//if(distancia[i][j][t-1][r]<= kMax) {
 									izq.addTerm(1, z[i][j][t-1][k][r+FlotaA+FlotaB+2]);
 								//}
 								//if(distancia[i][j][t][r]<= kMax) {
 		 							 der.addTerm(1, z[j][i][t][k][r+FlotaA+FlotaB+2]);
 		 						 //}
 							}
 							modelo.addEq(izq, der);
 							izq.clear();
 							der.clear();
 						}
 					}
 				}
 			}
 		 }
 		

 		System.out.println("Paso barrera 14");
 		
 		 //6.3.9 se considera que si un vehículo ocupa un arco determinado i a j (izquierda) en tiempo t-1,
 		 //entonces este queda posicionado en el nodo j (derecha) en tiempo t
 		

 		for (int t=1; t<= nPeriodos; t++) { 
 			for(int j=0; j<= nDemandas; j++) {
 				 for (int k=0; k <= nInstituciones;k++) {
 					 if(k==0) {
 						 for(int r=0; r<=FlotaA; r++) {
 							 for(int i =0; i<=nDemandas; i++) {
 								 //if(distancia[i][j][t-1][k]<= kMax) {
 									 izq.addTerm(1, z[i][j][t-1][k][r]);
 								 //}
 							 }
 							 der.addTerm(1, x[j][t][k][r]);
 							 modelo.addEq(izq, der);
 		 					 izq.clear();
 		 					 der.clear();
 		 					 
 						 }
 						 
 					 }
 					if(k==1) {
						 for(int r=0; r<=FlotaB; r++) {
							 for(int i =0; i<=nDemandas; i++) {
								 //if(distancia[i][j][t-1][k]<= kMax) {
									 izq.addTerm(1, z[i][j][t-1][k][r+FlotaA+1]);
								 //}
							 }
							 der.addTerm(1, x[j][t][k][r+FlotaA+1]);
 							 modelo.addEq(izq, der);
 		 					 izq.clear();
 		 					 der.clear();
						 }
						 
					 }
 					if(k==2) {
						 for(int r=0; r<=FlotaC; r++) {
							 for(int i =0; i<=nDemandas; i++) {
								 //if(distancia[i][j][t-1][k]<= kMax) {
									 izq.addTerm(1, z[i][j][t-1][k][r+FlotaA+FlotaB+2]);
								 //}
							 }
							 der.addTerm(1, x[j][t][k][r+FlotaA+FlotaB+2]);
 							 modelo.addEq(izq, der);
 		 					 izq.clear();
 		 					 der.clear();
						 }
						 
					 }
 				 }
 			 }
 		 }
 		 
 		System.out.println("Paso barrera 15");
 		
 		 // 6.3.10 permiten que una ambulancia se ubique en un nodo j, 
 		 //si y solo si está permitido que se puedan instalar ahí en el periodo t,  
 		 // y además que la cantidad de  ambulancias en nodo j no puede superar la capacidad del mismo. 
 		 
 		 for(int t=0; t<= nPeriodos; t++) {
 			 for (int j=0; j <= nDemandas; j++) {
 				 for(int r=0; r<= FlotaA; r++) {  //for de sumatoria
 					 izq.addTerm(1, x[j][t][0][r]);
 		 
 				 }
 				
 				 modelo.addLe(izq, disponibilidadAmb[j][t]*capacidadAmb[j]);
 				 izq.clear();
 				
 			 }
 		  }
 		System.out.println("Paso barrera 16");
/////////////////////////////////////////////////////////////////////////////////////////////////////
 		//6.3.11 RESTRICCION EXTRA para asegurar que los vehiculos en los nodos de inicio, coincidan con los arcos
 		//que conectan a los demás.
 		
 		for(int i=0; i <= nDemandas; i++) {
 			
 			for(int k=0; k<=nInstituciones; k++) {
 				if(k==0) {
 					for(int r=0; r<=FlotaA; r++) {
 						for(int j=0;j<= nDemandas;j++) {
 						der.addTerm(1, z[i][j][0][k][r]);
 					}
 						izq.addTerm(1,x[i][0][k][r]);
 						modelo.addEq(izq,der);
 						izq.clear();
 						der.clear();
 				  }
 				}
 				if(k==1) {
 					for(int r=0; r<=FlotaB; r++) {
 						for(int j=0;j<= nDemandas;j++) {
 						der.addTerm(1, z[i][j][0][k][r+FlotaA+1]);
 					}
 						izq.addTerm(1,x[i][0][k][r+FlotaA+1]);
 						modelo.addEq(izq,der);
 						izq.clear();
 						der.clear();
 				  }
 				}
 				if(k==2) {
 					for(int r=0; r<=FlotaC; r++) {
 						for(int j=0;j<= nDemandas;j++) {
 						der.addTerm(1, z[i][j][0][k][r+FlotaA+FlotaB+2]);
 					}
 						izq.addTerm(1,x[i][0][k][r+FlotaA+FlotaB+2]);
 						modelo.addEq(izq,der);
 						izq.clear();
 						der.clear();
 				  }
 				}
 			}
 		}
 		
 //////////////////////////////////////////////////////////////////////////////////////////////////////
 		 // 6.3.2 Representa evitar superar el respectivo presupuesto para la distancia recorrida (e-constraint)
 		
 		for(int i=0; i<=nDemandas; i++) {
 			for (int j=0; j <= nDemandas; j++) {
 				 for(int t=0; t<= nPeriodos-1; t++) {
 					for (int k=0; k<= nInstituciones; k++) {
 						if(k==0) {
 							for (int r=0; r<= FlotaA; r++) {
 								izq.addTerm(distancia[i][j][t][k],z[i][j][t][k][r] );
 							}
 						}
 						if(k==1) {
 							for (int r=0; r<= FlotaB; r++) {
 								izq.addTerm(distancia[i][j][t][k],z[i][j][t][k][r+FlotaA+1] );
 							}
 						}
 						if(k==2) {
 							for(int r=0; r<=FlotaC; r++) {
 								izq.addTerm( distancia[i][j][t][k],z[i][j][t][k][r+FlotaA+FlotaB+2]);
 							}
 						}
 					}
 				 }
 			}
 		}
 			modelo.addLe(izq, presupuesto);
 			izq.clear();
 		
 		
 		
 		//for (int k=0; k<= nInstituciones; k++) {
 			//if(k==0) {
 				// for (int r=0; r<= FlotaA; r++) {
 					// for(int t=0; t<= nPeriodos; t++) {
 						//for (int i=0; i <= nDemandas; i++) {
 							//for (int j=0; j <= nDemandas; j++) {
 
 								//izq.addTerm(z[i][j][t][k][r], distancia[i][j][t][k]);
// 						}		
 //				 }
 	//		}	
 		// }
 		//}
 			//if(k==1) {
				// for (int r=0; r<= FlotaB; r++) {
					// for(int t=0; t<= nPeriodos; t++) {
						//for (int i=0; i <= nDemandas; i++) {
							//for (int j=0; j <= nDemandas; j++) {
						
								//izq.addTerm(z[i][j][t][k][r+FlotaA+1], distancia[i][j][t][k]);
						//}		
			//	 }
			//}	
		 //}
		//}
 			//if(k==2) {
				// for (int r=0; r<= FlotaC; r++) {
					// for(int t=0; t<= nPeriodos; t++) {
						//for (int i=0; i <= nDemandas; i++) {
							//for (int j=0; j <= nDemandas; j++) {
						
								//izq.addTerm(z[i][j][t][k][r+FlotaA+FlotaB+2], distancia[i][j][t][k]);
						//}		
				 //}
			//}	
		 //}
		//}
 	//}	 
 	//	modelo.addLe(izq, presupuesto);
 		//izq.clear();
 		
 		System.out.println("Paso barrera 17");
 		
 		modelo.setParam(IloCplex.IntParam.NodeFileInd, 2);
 		modelo.setParam(IloCplex.IntParam.MIPSearch, 0);
		modelo.setParam(IloCplex.BooleanParam.MemoryEmphasis, true);
		modelo.setParam(IloCplex.DoubleParam.WorkMem, 3000); // tamaño memoria espacio de trabajo
		modelo.setParam(IloCplex.DoubleParam.EpGap, 0.0001); // Gap tolerable
		
		modelo.exportModel("C:/Users/Indy Navarro/eclipse-workspace/LocalizacionFlota3/instancia"+ o +".lp");
		
		if(modelo.solve()) {
			WritableWorkbook libro= Workbook.createWorkbook(new File (("C:/Users/Indy Navarro/eclipse-workspace/LocalizacionFlota3/Resultados"+ o +".xls")));
			WritableSheet hoja=libro.createSheet("Resultados", 0);
	 		hoja.addCell(new Label(0,0,"Función Objetivo"));
	 		hoja.addCell(new Number(1,0,modelo.getObjValue()));
	 		int m=0;
	 		
	 		//Insertar valor asociado de costo 
	 		
	 		for (int k=0; k<= nInstituciones; k++) {
	 			if(k==0) {
	 				 for (int r=0; r<= FlotaA; r++) {
	 					 for(int t=0; t<= nPeriodos-1; t++) {
	 						for (int i=0; i <= nDemandas; i++) {
	 							for (int j=0; j <= nDemandas; j++) {
	 						
	 								costo= costo+ modelo.getValue(z[i][j][t][k][r])*distancia[i][j][t][k];
	 						}		
	 				 }
	 			}	
	 		 }
	 		}
	 			if(k==1) {
					 for (int r=0; r<= FlotaB; r++) {
						 for(int t=0; t<= nPeriodos-1; t++) {
							for (int i=0; i <= nDemandas; i++) {
								for (int j=0; j <= nDemandas; j++) {
							
									costo= costo+ modelo.getValue(z[i][j][t][k][r+FlotaA+1])*distancia[i][j][t][k];
							}		
					 }
				}	
			 }
			}
	 			if(k==2) {
					 for (int r=0; r<= FlotaC; r++) {
						 for(int t=0; t<= nPeriodos-1; t++) {
							for (int i=0; i <= nDemandas; i++) {
								for (int j=0; j <= nDemandas; j++) {
							
									costo= costo+ modelo.getValue(z[i][j][t][k][r+FlotaA+FlotaB+2])*distancia[i][j][t][k];
							}		
					 }
				}	
			 }
			}
	 	}	 
	 		
	 		hoja.addCell(new Label(0,1,"costo"));
	 		hoja.addCell(new Number(1,1,costo));
	 		costo=0;  //Limpiar para la siguiente instancia
	 		
	 		//Insertar columna de valores de y.

	 		hoja.addCell(new Label(3,m,"y"));
	 		hoja.addCell(new Label(4,m,"i"));
	 		hoja.addCell(new Label(5,m,"t"));
	 		for(int i=0; i <=nDemandas; i++) {
	 			for( int t=0; t<= nPeriodos; t++) {
	 				hoja.addCell(new Number(4,m+1,i));
	 				hoja.addCell(new Number(5,m+1,t));
	 				//hoja.addCell(new Label (3,m,"y" + i + j));
 					hoja.addCell(new Number(6,m+1,modelo.getValue(y[i][t])));
 					m++;
	 			
	 			}
	 		}
	 		
	 		m=0;
	 		
	 		//Insertar valores de x
	 		hoja.addCell(new Label(8,m,"x"));
	 		hoja.addCell(new Label(9,m,"i"));
	 		hoja.addCell(new Label(10,m,"t"));
	 		hoja.addCell(new Label(11,m,"k"));
	 		hoja.addCell(new Label(12,m,"r"));
	 		for(int i=0; i <=nDemandas; i++) {
	 			for( int t=0; t<= nPeriodos; t++) {
	 				for(int k=0; k <= nInstituciones; k++) {
	 					if(k==0) {
	 						for(int r=0; r<=FlotaA; r++) {
	 							hoja.addCell(new Number(9,m+1,i));
	 							hoja.addCell(new Number(10,m+1,t));
	 							hoja.addCell(new Number(11,m+1,k));
	 							hoja.addCell(new Number(12,m+1,r));
	 							hoja.addCell(new Number(13,m+1,modelo.getValue(x[i][t][k][r])));
	 							
			 					m++;
	 						}
	 						
	 					}
	 					if(k==1) {
	 						for(int r=0; r<=FlotaB; r++) {
	 							hoja.addCell(new Number(9,m+1,i));
	 							hoja.addCell(new Number(10,m+1,t));
	 							hoja.addCell(new Number(11,m+1,k));
	 							hoja.addCell(new Number(12,m+1,r));
	 							hoja.addCell(new Number(13,m+1,modelo.getValue(x[i][t][k][r+FlotaA+1])));
	 							
	 							
			 					m++;
	 						}
	 					}
	 					if(k==2) {
	 						for(int r=0; r<=FlotaC; r++) {
	 							hoja.addCell(new Number(9,m+1,i));
	 							hoja.addCell(new Number(10,m+1,t));
	 							hoja.addCell(new Number(11,m+1,k));
	 							hoja.addCell(new Number(12,m+1,r));
	 							hoja.addCell(new Number(13,m+1,modelo.getValue(x[i][t][k][r+FlotaA+FlotaB+2])));
	 							
			 					m++;
	 						}
	 					}
	 					
	 			  }
	 			}
	 		}
	 		
	 		m=0;
	 		
	 		//Insertar valores de z
	 		hoja.addCell(new Label(15,m,"z"));
	 		hoja.addCell(new Label(16,m,"i"));
	 		hoja.addCell(new Label(17,m,"j"));
	 		hoja.addCell(new Label(18,m,"t"));
	 		hoja.addCell(new Label(19,m,"k"));
	 		hoja.addCell(new Label(20,m,"r"));
	 		
	 		for(int i=0; i <=nDemandas; i++) {
	 			for( int j=0; j<= nDemandas; j++) {
	 				for(int t=0; t <= nPeriodos-1; t++) {
	 					for(int k=0; k <= nInstituciones; k++) {
	 						if(k==0) {
	 							for(int r=0; r<= FlotaA; r++) {
	 								
	 						 		hoja.addCell(new Number(16,m+1,i));
	 						 		hoja.addCell(new Number(17,m+1,j));
	 						 		hoja.addCell(new Number(18,m+1,t));
	 						 		hoja.addCell(new Number(19,m+1,k));
	 						 		hoja.addCell(new Number(20,m+1,r));
	 			 					hoja.addCell(new Number(21,m+1,modelo.getValue(z[i][j][t][k][r])));
	 			 					m++;
	 							}
	 						}
	 						if(k==1) {
	 							for(int r=0; r<= FlotaB; r++) {
	 								hoja.addCell(new Number(16,m+1,i));
	 						 		hoja.addCell(new Number(17,m+1,j));
	 						 		hoja.addCell(new Number(18,m+1,t));
	 						 		hoja.addCell(new Number(19,m+1,k));
	 						 		hoja.addCell(new Number(20,m+1,r));
	 			 					hoja.addCell(new Number(21,m+1,modelo.getValue(z[i][j][t][k][r+FlotaA+1])));
	 			 					m++;
	 							}
	 						}
	 						if(k==2) {
	 							for(int r=0; r<= FlotaC; r++) {
	 								hoja.addCell(new Number(16,m+1,i));
	 						 		hoja.addCell(new Number(17,m+1,j));
	 						 		hoja.addCell(new Number(18,m+1,t));
	 						 		hoja.addCell(new Number(19,m+1,k));
	 						 		hoja.addCell(new Number(20,m+1,r));
	 			 					hoja.addCell(new Number(21,m+1,modelo.getValue(z[i][j][t][k][r+FlotaA+FlotaB+2])));
	 			 					m++;
	 							}
	 						}
	 							 				
	 				}
	 			  }
	 			}
	 		}
			
	 		m=0;
	 		
	 		//Insertar valores de v
	 		hoja.addCell(new Label(23,m,"v"));
	 		hoja.addCell(new Label(24,m,"i"));
	 		hoja.addCell(new Label(25,m,"t"));
	 		hoja.addCell(new Label(26,m,"k"));
	 		for(int i=0; i <=nDemandas; i++) {
	 			for( int k=0; k<= nInstituciones; k++) {
	 				for(int t=0; t <= nPeriodos;t++) {
	 					hoja.addCell(new Number(24,m+1,i));
	 					hoja.addCell(new Number(25,m+1,t));
	 					hoja.addCell(new Number(26,m+1,k));
	 					hoja.addCell(new Number(27,m+1,modelo.getValue(v[i][t][k])));
	 					m++;
	 	
	 			  }
	 			}
	 		}
	 		
	 		libro.write();
	 		libro.close();
	 		//Incrementar o para el número de páginas de excel y el presupuesto
	 		o++;
	 		endTimeInstance = System.nanoTime();
			System.out.println("el tiempo  de la instancia número"+u+ "es :");
			System.out.println(endTimeInstance - startTimeInstance);
			
		}

		System.out.println("Paso barrera 4");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("Falló la lectura de parámetros");
		}
	   presupuesto= presupuesto +100;
	   u=u+1;
		}
		long endTime = System.nanoTime();
		System.out.println("el tiempo de proceso es:");
		System.out.println(endTime - startTime);
	}
}
