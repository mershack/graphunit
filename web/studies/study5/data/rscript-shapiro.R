setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\studies\\study5\\data")
sink("rscript-shapiro.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
Acc_neighbor_count_graph= c(accuracy1[,1])
shapiro_Acc_neighbor_count_graph= shapiro.test(Acc_neighbor_count_graph)
Acc_neighbor_count_matrix= c(accuracy2[,1])
shapiro_Acc_neighbor_count_matrix= shapiro.test(Acc_neighbor_count_matrix)
taskname="TaskName = Acc_neighbor_count"
taskname
paste("Acc_neighbor_count_graph ," , shapiro_Acc_neighbor_count_graph$p.value)
paste("Acc_neighbor_count_matrix ," , shapiro_Acc_neighbor_count_matrix$p.value)
"*********************"
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
Time_neighbor_count_graph= c(time1[,1])
shapiro_Time_neighbor_count_graph= shapiro.test(Time_neighbor_count_graph)
Time_neighbor_count_matrix= c(time2[,1])
shapiro_Time_neighbor_count_matrix= shapiro.test(Time_neighbor_count_matrix)
taskname="TaskName = Time_neighbor_count"
taskname
paste("Time_neighbor_count_graph ," , shapiro_Time_neighbor_count_graph$p.value)
paste("Time_neighbor_count_matrix ," , shapiro_Time_neighbor_count_matrix$p.value)
"*********************"
sink()
