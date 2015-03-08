setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\studies\\study5\\data")
sink("rscript-meanAndSD.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
Acc_neighbor_count_graph= c(accuracy1[,1])
mean_Acc_neighbor_count_graph= mean(Acc_neighbor_count_graph)
sd_Acc_neighbor_count_graph= sd(Acc_neighbor_count_graph)
Acc_neighbor_count_matrix= c(accuracy2[,1])
mean_Acc_neighbor_count_matrix= mean(Acc_neighbor_count_matrix)
sd_Acc_neighbor_count_matrix= sd(Acc_neighbor_count_matrix)
taskname="TaskName = Acc_neighbor_count"
taskname
paste("Task Name ," ,"Mean ," , "Standard-Deviation")
"------------------------------------------------------"
paste("Acc_neighbor_count_graph ," , mean_Acc_neighbor_count_graph , "," , sd_Acc_neighbor_count_graph)
paste("Acc_neighbor_count_matrix ," , mean_Acc_neighbor_count_matrix , "," , sd_Acc_neighbor_count_matrix)
"*******************************************************"
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
Time_neighbor_count_graph= c(time1[,1])
mean_Time_neighbor_count_graph= mean(Time_neighbor_count_graph)
sd_Time_neighbor_count_graph= sd(Time_neighbor_count_graph)
Time_neighbor_count_matrix= c(time2[,1])
mean_Time_neighbor_count_matrix= mean(Time_neighbor_count_matrix)
sd_Time_neighbor_count_matrix= sd(Time_neighbor_count_matrix)
taskname="TaskName = Time_neighbor_count"
taskname
paste("Task Name ," ,"Mean ," , "Standard-Deviation")
"------------------------------------------------------"
paste("Time_neighbor_count_graph ," , mean_Time_neighbor_count_graph , "," , sd_Time_neighbor_count_graph)
paste("Time_neighbor_count_matrix ," , mean_Time_neighbor_count_matrix , "," , sd_Time_neighbor_count_matrix)
"*******************************************************"
sink()
