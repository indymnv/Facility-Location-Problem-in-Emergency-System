# optimization
This project was part of my master's thesis completed in 2019. The problem dealt with three bi-objective optimization models that addressed the collaborative work between the three emergency institutions of San Antonio's city in Chile and these institutions are: SAMU (related to ambulances), Fire Brigade and Police. Considering that there are many emergencies in which these institutions work collaboratively, their location should be verified based on the LSCP and MCLP models to ensure the most significant coverage and at the lowest possible cost. This thesis also considers the demands that occur in a time horizon with real data obtained from one of the city's fire stations. 

For this, the following 3 models that were left in each of the files are considered

1. It is **necessary** to cover the population with these three institutions. It has a budget
2. It is **desired** to cover the population with these three institutions, and it is recompensed, which means the more institutions cover a specific place, the higher value they will get.
3. The last model is different from the first two since a known fleet of each of the institutions is considered and a time horizon where the possible demand of the sector over time is known, which is dynamic. Then it is considered that there are dynamic flows in the time horizons between different nodes. This model is simulating possible traffic jams on the different routes between time intervals. What is sought here is increasing demand coverage and considering the decrease in costs associated with moving a vehicle to another area—this, of course, considering certain particularities of each institution.

The problems were solved with Java+Cplex and some visualizations were made in Tableau.
