package modeloLocalizacion1;
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/// Esta área es originalmente para el codigo del caso 1
import ilog.concert.*;
import java.util.*;
import ilog.cplex.*;
import java.io.*;
import java.io.File;
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
		int u =400
				; 
		
		 int o = 1; 
		 long startTime = System.nanoTime();
		 
		
		
	//conjunto de parametros
		double [] demanda=null;
		double [] cobertura=null;
		double [][] distancia=null;
		double [][] costo=null;
		
		int nInstituciones=0;  
		int nLocalizaciones=0;
		int nDemandas=0;
		double precio=0; 
		//conjuntos del problema
	
		                  
		long presupuesto=1000;
		//Este paso es para lectura
		//----------------------------------------------------//
		
		try {
		System.out.println("Inicio de lectura");
		Workbook libro=Workbook.getWorkbook(new File("C:/Users/Indy Navarro/eclipse-workspace/modeloLocalizacion1/Instancia_orig.xls")); //Archivo a usar
		System.out.println("Acceso al archivo");
		
	                     
			
		Sheet hoja=libro.getSheet(0);
		//System.out.println("Acceso a hoja 1");
		nLocalizaciones= Integer.parseInt(hoja.getCell(1,5).getContents()); //    ((NumberCell)((Cell)(hoja.getCell(col,fila)))).getValue();
		
		
		System.out.println(nLocalizaciones);
		nDemandas= Integer.parseInt(hoja.getCell(1,3).getContents());
		
		System.out.println(nDemandas);
		nInstituciones= Integer.parseInt(hoja.getCell(1,4).getContents());
		System.out.println(nInstituciones);
		//presupuesto= Integer.parseInt(hoja.getCell(1,6).getContents());
		System.out.println(presupuesto);

	
			
		//System.out.println("lectura de parametros");
		
		demanda= new double [nDemandas+1];
		cobertura= new double [nInstituciones+1];
		costo= new double [nLocalizaciones+1][nInstituciones+1];
		distancia= new double [nDemandas+1][nLocalizaciones+1];
		
		//System.out.println("definición largo de vectores y matrices");
		
		for(int i=0; i<=nDemandas; i++ ) {
			demanda[i]=Double.parseDouble(hoja.getCell(2,91+i).getContents());
			
			//demanda[i][t]= (Math.random() * ((50) + 1)) + 50;
			//demanda[i]=(Math.random() * ((20000-5000) + 1)) + 5000;
			//System.out.println("demanda "+ i + demanda[i]);
			
		}
		for(int k=0; k<=nInstituciones; k++ ) {
			cobertura[k]=Double.parseDouble(hoja.getCell(2,172 + k).getContents());
			//System.out.println("cobertura "+ k + cobertura[k]);
			//cobertura[k]=(Math.random() * ((4000-2000) + 1)) + 2000;
		}
		for(int j=0; j<=nLocalizaciones; j++) {
			for(int k=0; k<=nInstituciones; k++) {
				costo[j][k]= Double.parseDouble(hoja.getCell(2+k , 177+j).getContents());
				//System.out.println("costo "+j+ k + costo[j][k]);
				//costo[j][k]=(Math.random() * ((800-300) + 1)) + 300;
			}
		}
		for(int i=0; i<=nDemandas; i++) {
			for(int j=0; j<=nLocalizaciones; j++) {
				distancia[i][j]= Double.parseDouble(hoja.getCell( 2+j,10 +i).getContents());
			    //System.out.println("distancia "+i+ j + " "+ distancia[i][j]);
				//if (i==j){
				//distancia[i][j]=0;
			//}
			//distancia[i][j]=(Math.random() * ((20000-5000) + 1)) + 500;
			}
		}
		System.out.println("Lectura de vectores y matrices");
	
		
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("Falló la lectura de parámetros");
				
		}
		
		//-------------------------------------------------------------------------//Borrar do while
		while (presupuesto <= 3000) {
			System.out.println("fase n°" + u);  
		try {
			
			  //Creación modelo
	  	  		IloCplex modelo = new IloCplex(); 
	  	  	//System.out.println("Se creó el modelo");
	  	  	  //Variable
	  	  	    IloIntVar[] x = new IloIntVar[nDemandas+1]; 
	  	  	    //1 si hay múltiple cobertura de las 3 instituciones en i; 0EC
	  	  	    IloIntVar[][] y = new IloIntVar[nDemandas+1][nInstituciones+1];  //1 si nodo i esta cubierto por institucion k; 0 EOC 
	  	  	    IloIntVar[][] z = new IloIntVar[nLocalizaciones+1][nInstituciones+1]; //1 si nodo j se localiza una inst k; 0 EOC
	  	  	    
	  	  	System.out.println("Creación de variables");
	  	  	  //Variables definidas como Binarias
	  	  	    for (int i=0; i<= nDemandas; i++) {
	  	  	    x[i] = modelo.intVar(0,1);
	  	  	  //  System.out.println(x[i]);
	  	  	    }
	  	  	//x[i]=modelo.numVarArray(nLocalizaciones, 0, 1);
	  	  	//System.out.println("Creación primera variable");
	  	  	    for (int i=0; i<= nDemandas; i++)
	  	  	    	for (int k=0; k <= nInstituciones; k++) 
	  	  	    	 y[i][k] = modelo.intVar(0,1);
	  	  	//System.out.println("Creación segunda variable");	  
	  	  	    for (int j=0; j<= nLocalizaciones; j++) 
	  	  	    	for (int k=0; k <= nInstituciones; k++)
	  	  	    		  z[j][k] = modelo.intVar(0,1);
	  	  	     
	  	  	//System.out.println("Definición de variables binarias");
	  	  	
	  	  
	  	  	    
	  	  	  //Creación de expresiones
		    	 IloLinearNumExpr objetivo = modelo.linearNumExpr(); 
		 		 IloLinearNumExpr izq = modelo.linearNumExpr();
		 		 IloLinearNumExpr der = modelo.linearNumExpr();
		 	
		 	//System.out.println("Creación de expresiones");
		 	
		 	//Función objetivo: Maximizar la demanda cubierta.	 
		 		 for (int i=0; i<= nDemandas; i++) {  
		 			 objetivo.addTerm(demanda[i], x[i]); //Agregar constante, variable decision
		 			 //System.out.println(i);
		 			 for(int k=0; k <= nInstituciones; k++)
		 				 objetivo.addTerm(1, y[i][k]);
		 		 }
		 			 modelo.addMaximize(objetivo); //FO
		 		 objetivo.clear();    //Limpiar
		 		 
		 		
		 		 //Restricción 1:Para cada nodo de demanda i, este se encontrará cubierto si y solo si está cubierto por cada institución
		 		 
		 		for (int i=0; i<= nDemandas; i++ ) {
		 			 for (int k=0; k<= nInstituciones; k++) {
		 				 izq.addTerm(1, y[i][k]);
		 				 der.addTerm(1, x[i]);
		 				 modelo.addGe(izq,der);
				 		 izq.clear();
				 		 der.clear();
		 		  }
		 		 }

		 		
		 		 //Restricción 2: cada nodo i está cubierto por una institución k si y solo si 
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
		 		
		 		 //Restricción 3: Para todo Nodo de localización se debe asignar una única instalación
		 		 
		 		 for(int j=0; j<= nLocalizaciones; j++) {
		 			 for(int k=0; k<= nInstituciones; k++) {
		 
		 				 izq.addTerm(1, z[j][k]);
		 			 }
		 			 modelo.addLe(izq, 1);
			 		 izq.clear();
		 		 }
		 		 
		 		
		 		 // Restricción 4: Los costos de localización no deben 
		 		 // ser superados por un presupuesto (e-constraint)
		 		 
		 		 for (int k=0; k<= nInstituciones; k++) {
		 			 for (int j=0; j<= nLocalizaciones; j++) {
		 				 izq.addTerm(z[j][k],costo[j][k] );
		 				 
		 			 }
		 		 }
		 		 
		 		 modelo.addLe(izq, presupuesto);
		 		 izq.clear();
		 		 
		 		 
		 		 
		 		 
		 		 //System.out.println("Creación Todas las restricciones");
		 		 //Escritura de resultados
		 		 //-----------------------------------------------------------------//
		 		//modelo.setParam(IloCplex.StringParam.WorkDir,"C:/Cplex");
		 		modelo.setParam(IloCplex.IntParam.NodeFileInd, 2);
		 		modelo.setParam(IloCplex.IntParam.MIPSearch, 0);
				//cplex.setParam(IloCplex.StringParam.WorkDir, "C:/Arbol_Cplex/");
				modelo.setParam(IloCplex.BooleanParam.MemoryEmphasis, true);
				//cplex.setParam(IloCplex.IntParam.NodeFileInd, 3);// 2.Archivo nodo en disco - 3. Archivo nodo comprimido en disco
				modelo.setParam(IloCplex.DoubleParam.WorkMem, 1500); // tamaño memoria espacio de trabajo
				modelo.setParam(IloCplex.DoubleParam.EpGap, 0.1); // Gap tolerable
				//			cplex.setParam(IloCplex.DoubleParam.TreLim, 3072);// limite de memoria del arbol para b&c
				//			cplex.setParam(IloCplex.DoubleParam.TiLim, 600);// tiempo limite
				//			cplex.exportModel(carpetaArchivos + "HR/modelo1.lp");
				//
		 		 
		 		modelo.exportModel("C:/Users/Indy Navarro/eclipse-workspace/modeloLocalizacion1/instancia"+ o +".lp");
		 		
		 		
		 		 if(modelo.solve()) {
		 		WritableWorkbook libro= Workbook.createWorkbook(new File (("C:/Users/Indy Navarro/eclipse-workspace/modeloLocalizacion1/Resultados"+ o +".xls")));
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
		 			hoja.addCell(new Label (3,m,"x" + i));
 					hoja.addCell(new Number(4,m,modelo.getValue(x[i])));
 					m++;
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
		 		presupuesto = presupuesto + 500;
		}
		}catch (Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("Falló la escritura de resultados");

		}
		
		}
		 long endTime = System.nanoTime();
		System.out.println("el tiempo de proceso es:");
		System.out.println(endTime - startTime);
}
		
}
		


