//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Esta área es originalmente para el codigo del caso 2

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

public class Problema {

	public static void main(String[] args)  throws IOException {
		
		long startTime=System.nanoTime();
		int u=0;
		int o=1;
		long promedio;
		long startTimeInstance;
		long endTimeInstance;
		long delta=0;
		int contador=1;
		double precio=0;
		
		//conjunto de parametros
			double [][] demanda=null;
			double [] cobertura=null;
			double [][] distancia=null;
			double [][] costo=null;
		
		//conjuntos del problema
			int nInstituciones=0;  
			int nLocalizaciones=0;
			int nDemandas=0;
			double presupuesto=0;
			
		//Espacio para lectura de parámetros en excel
			
			try {
				Workbook libro=Workbook.getWorkbook(new File("C:/Users/Indy Navarro/eclipse-workspace/ModeloLocalizacion2.5/instancia.xls")); //Archivo a usar
				Sheet hoja=libro.getSheet(0);
				nLocalizaciones= Integer.parseInt(hoja.getCell(1,4).getContents()); //    ((NumberCell)((Cell)(hoja.getCell(col,fila)))).getValue();
				System.out.println(nLocalizaciones);
				//nDemandas=300;
				nDemandas= Integer.parseInt(hoja.getCell(1,3).getContents());
				//System.out.println(nDemandas);
				nInstituciones= Integer.parseInt(hoja.getCell(1,2).getContents());
				//nInstituciones=2;
				System.out.println(nInstituciones);
				presupuesto= Integer.parseInt(hoja.getCell(1,5).getContents());
				//presupuesto=200;
				
				//System.out.println(presupuesto);
				
				
				demanda= new double [nDemandas+1][nInstituciones +1];
				cobertura= new double [nInstituciones+1];
				costo= new double [nLocalizaciones+1][nInstituciones+1];
				distancia= new double [nDemandas+1][nLocalizaciones+1];
				
				for(int i=0; i<=nDemandas; i++ ) {
					for(int k=0; k<=nInstituciones; k++ ) {
					//demanda[i][k]=Double.parseDouble(hoja.getCell(2+i,8+k).getContents());
					
					
				    demanda[i][k]=(Math.random() * ((20000-5000) + 1)) + 5000;
				}
				}
				for(int j=0; j<=nLocalizaciones; j++) {
					for(int k=0; k<=nInstituciones; k++) {
						//costo[j][k]= Double.parseDouble(hoja.getCell(2+j , 13+k).getContents());
						//System.out.println("costo "+j+ k +" " + costo[j][k]);
						costo[j][k]=(Math.random() * ((800-300) + 1)) + 300;
					}
				}
				for(int i=0; i<=nDemandas; i++) {
					for(int j=0; j<=nLocalizaciones; j++) {
						//distancia[i][j]= Double.parseDouble(hoja.getCell(2+j,23 +i).getContents());
						//System.out.println("distancia "+i+ j + " "+ distancia[i][j]);
						if (i==j){
						distancia[i][j]=0;
					}
					distancia[i][j]=(Math.random() * ((15000-500) + 1)) + 500;
					}
				}
				for(int k=0; k<=nInstituciones; k++ ) {
					cobertura[k]=Double.parseDouble(hoja.getCell(2,18 + k).getContents());
					//System.out.println("cobertura "+ k +" "+ cobertura[k]);
					//cobertura[k]=(Math.random() * ((4000-2000) + 1)) + 2000;
				}
			
			
				
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(e.getMessage());
				System.out.println("Falló la lectura de parámetros");
			}
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			   
			while (contador <= 5) {
				System.out.println("fase n°" + u); 
				startTimeInstance=System.nanoTime();
				try {
					
					IloCplex modelo = new IloCplex(); 
					
					    //Variables
					    IloIntVar[][] x = new IloIntVar[nDemandas+1][nInstituciones+1];  //1 si nodo i esta cubierto por n instituciones;0 0EC
			  	  	    IloIntVar[][] y = new IloIntVar[nDemandas+1][nInstituciones+1];  //1 si nodo i esta cubierto por institucion k; 0 EOC 
			  	  	    IloIntVar[][] z = new IloIntVar[nLocalizaciones+1][nInstituciones+1]; //1 si nodo j se localiza una inst k; 0 EOC

			  	  	    String nomX[][]=new String[nDemandas + 1][nInstituciones + 1];
			  	  	    String nomY[][]=new String[nDemandas + 1][nInstituciones + 1];
			  	  	    String nomZ[][]=new String[nLocalizaciones + 1][nInstituciones + 1];
			  	  	    
			  	  	    for( int i=0; i <= nDemandas; i++) {
			  	  	    	for(int n=0; n<=nInstituciones; n++) {
			  	  	    	 nomX[i][n]="x_"+(i)+"_"+(n);
					  	  	 nomY[i][n]="y_"+(i)+"_"+(n);
			  	  	    	}
			  	  	    }
			  	  	    
			  	  	    for(int j=0; j<= nLocalizaciones; j++) {
			  	  	    	for(int n=0; n<=nInstituciones; n++) {
			  	  	    	nomZ[j][n]="y_"+(j)+"_"+(n);
			  	  	    	}
			  	  	    }
			  	  	System.out.println("Creación de variables");
			  	  	
			  	  	  //Variables definidas como Binarias
			  	  	    for (int i=0; i<= nDemandas; i++) {
			  	  	    	for(int n=0; n <= nInstituciones; n++) {
			  	  	    		x[i][n] = modelo.intVar(0,1);
			  	  	  //  System.out.println(x[i]);
			  	  	    }
			  	  	   }
			  	  	    
			  	  	    for (int i=0; i<= nDemandas; i++)
			  	  	    	for (int k=0; k <= nInstituciones; k++) 
			  	  	    	 y[i][k] = modelo.intVar(0,1);
			  	  	
			  	  	    //System.out.println("Creación segunda variable");	  
			  	  	    for (int j=0; j<= nLocalizaciones; j++) 
			  	  	    	for (int k=0; k <= nInstituciones; k++)
			  	  	    		  z[j][k] = modelo.intVar(0,1);
			  	  	    
			  	  	 //Creación de expresiones
				    	 IloLinearNumExpr objetivo = modelo.linearNumExpr(); 
				 		 IloLinearNumExpr izq = modelo.linearNumExpr();
				 		 IloLinearNumExpr der = modelo.linearNumExpr();
				 		 
				 		//Función objetivo: Maximizar la demanda cubierta.	 
				 		 for (int i=0; i<= nDemandas; i++) {  
				 			 for(int n=0; n <= nInstituciones; n++) { 
				 			 objetivo.addTerm(demanda[i][n], x[i][n]);
				 			 objetivo.addTerm(0.0001, y[i][n]);
				 			//Agregar constante, variable decision
				 			 //System.out.println(i);
				 			
				 		 }
				 		 }
				 			 modelo.addMaximize(objetivo); //FO
				 		 objetivo.clear();    //Limpiar
					
				

				 		 //Restricción 1: cada nodo i está cubierto por una institución k si y solo si 
				 		 //se posiciona la institución k dentro del conjunto cobertura de nodos que 
				 		 //pueden satisfacer la demanda dentro del radio de cobertura,
				 		  
				 		 
				 		 for (int i=0; i<= nDemandas; i++) {
				 			 for (int k=0; k <= nInstituciones; k++) {
				 				 for (int j =0; j <= nLocalizaciones; j++) {          //For de sumatoria
				 					 if (distancia[i][j] <= cobertura[k]) {
				 						 izq.addTerm(1, z[j][k]);
				 					 }
				 				 }
				 				der.addTerm(1, y[i][k]);
				 				modelo.addGe(izq, der);
						 	    izq.clear();
						 		der.clear();
				 			 }
				 		 }
				 		 
				 		 //Restricción 2: Permite mostrar que cada nodo i existan n instituciones,
				 		 //que lo esten cubriendo y este sea contado
				 		
				 		 for (int i =0; i <= nDemandas; i++) {
				 			 for (int n=0; n <= nInstituciones; n++) {
				 				 izq.addTerm(n+1, x[i][n]);        // Se aplica n+1 para multiplicar desde 1
				 				 der.addTerm(1, y[i][n]);
				 			 }
				 			modelo.addLe(izq, der);
				 			izq.clear();
					 		der.clear();
				 		 }
				 	
				 		 
				 		//Restricción 3: Para todo Nodo de localización se debe asignar una única instalación
				 		 
				 		 for(int j=0; j<= nLocalizaciones; j++) {
				 			 for(int k=0; k<= nInstituciones; k++) {
				 
				 				 izq.addTerm(1, z[j][k]);
				 			 }
				 			 modelo.addLe(izq, 1);
					 		 izq.clear();
				 		 }
				 		 
				 		 //Restricción 4: Obliga a que la variable x solo tome un valor de n, dado que se desea
				 		 //que la variable nos indique cuantas instituciones n estan cubriendo a nodo i.
				 		 
				 		 for(int i =0; i <= nDemandas; i++) {
				 			 for(int n=0; n <=nInstituciones; n++) {
				 				 izq.addTerm(1, x[i][n]);
				 				 
				 			 }
				 			 modelo.addLe(izq, 1);
				 			 izq.clear();
				 		 }
				 		 
				 		 // Restricción 5: Los costos de localización no deben 
				 		 // ser superados por un presupuesto (e-constraint)
				 		 
				 		 for (int k=0; k<= nInstituciones; k++) {
				 			 for (int j=0; j<= nLocalizaciones; j++) {
				 				 izq.addTerm(z[j][k],costo[j][k] );
				 				 
				 			 }
				 		 }
				 		 
				 		 modelo.addLe(izq, presupuesto);
				
				 		izq.clear();
				 		
				 		 //Escritura de resultados
				 		 //-----------------------------------------------------------------//
				 		
				 		modelo.setParam(IloCplex.IntParam.NodeFileInd, 2);
				 		modelo.setParam(IloCplex.IntParam.MIPSearch, 0);
						modelo.setParam(IloCplex.BooleanParam.MemoryEmphasis, true);
						modelo.setParam(IloCplex.DoubleParam.WorkMem, 1500); // tamaño memoria espacio de trabajo
						modelo.setParam(IloCplex.DoubleParam.EpGap, 0.1); // Gap tolerable

				 		 
				 		modelo.exportModel("C:/Users/Indy Navarro/eclipse-workspace/ModeloLocalizacion2.5/instancia"+ o +".lp");
				 		
				 		 if(modelo.solve()) {
						 		WritableWorkbook libro= Workbook.createWorkbook(new File (("C:/Users/Indy Navarro/eclipse-workspace/ModeloLocalizacion2.5/Resultados"+ o +".xls")));
						 		WritableSheet hoja=libro.createSheet("Resultados", 0);
						 		hoja.addCell(new Label(0,0,"Función Objetivo"));
						 		hoja.addCell(new Number(1,0,modelo.getObjValue()));
						 		hoja.addCell(new Label(0,1,"Costo"));
						 		for(int k=0; k<=nInstituciones;k++) {
						 			for(int j=0; j<= nLocalizaciones;j++) {
						 				precio= precio + modelo.getValue(z[j][k])*costo[j][k];
						 			}
						 		}
						 		hoja.addCell(new Number(1,1,precio));
						 		precio=0;
						 		int m=0;
						 		for (int i=0; i<=nDemandas; i++) {
						 			for(int n=0; n <=nInstituciones; n++) {
						 			hoja.addCell(new Label (3,m,"x" + i + n));
				 					hoja.addCell(new Number(4,m,modelo.getValue(x[i][n])));
				 					m++;
						 		}
						 		}
						 		m=0;
						 		for (int i=0; i<=nDemandas; i++) {		
						 			for (int k=0; k<=nInstituciones;k++) {
						 					hoja.addCell(new Label (6,m,"y" + i+" "+k));
						 					hoja.addCell(new Number(7,m,modelo.getValue(y[i][k])));
						 					m++;
						 			}
						 		}
						 		m=0;
						 		for (int j=0; j<=nLocalizaciones; j++) {
									for (int k=0; k<=nInstituciones;k++) {
						 					hoja.addCell(new Label (9,m,"z"+j+" " + k));
						 					hoja.addCell(new Number(10,m,modelo.getValue(z[j][k])));
						 					m++;
						 				}
						 				
						 			}
						 			 
						 		 
						 		libro.write();
						 		libro.close();
						 		//Incrementar o para el número de páginas de excel y el presupuesto
						 		o++;
						 		u=u+100;
						 		presupuesto = presupuesto + 300;
						}
				 		
			}catch (Exception e){
				e.printStackTrace();
				System.out.println(e.getMessage());
				System.out.println("Falló la escritura de resultados");

			}
				endTimeInstance=System.nanoTime();
				delta=delta+(endTimeInstance-startTimeInstance);
				contador ++;
			}
			long endTime = System.nanoTime();
			System.out.println("El tiempo de proceso es: ");
			System.out.println(endTime - startTime);
		
			
	}
}
