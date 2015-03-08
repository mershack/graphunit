setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\studies\\study6\\data")
sink("rscript-meanAndSD.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
accuracy3 = read.csv("AccuracyResults3.txt")
Acc_neighbor_one_step_arrow= c(accuracy1[,1])
mean_Acc_neighbor_one_step_arrow= mean(Acc_neighbor_one_step_arrow)
sd_Acc_neighbor_one_step_arrow= sd(Acc_neighbor_one_step_arrow)
Acc_neighbor_one_step_tapered= c(accuracy2[,1])
mean_Acc_neighbor_one_step_tapered= mean(Acc_neighbor_one_step_tapered)
sd_Acc_neighbor_one_step_tapered= sd(Acc_neighbor_one_step_tapered)
Acc_neighbor_one_step_circular= c(accuracy3[,1])
mean_Acc_neighbor_one_step_circular= mean(Acc_neighbor_one_step_circular)
sd_Acc_neighbor_one_step_circular= sd(Acc_neighbor_one_step_circular)
taskname="TaskName = Acc_neighbor_one_step"
taskname
paste("Task Name ," ,"Mean ," , "Standard-Deviation")
"------------------------------------------------------"
paste("Acc_neighbor_one_step_arrow ," , mean_Acc_neighbor_one_step_arrow , "," , sd_Acc_neighbor_one_step_arrow)
paste("Acc_neighbor_one_step_tapered ," , mean_Acc_neighbor_one_step_tapered , "," , sd_Acc_neighbor_one_step_tapered)
paste("Acc_neighbor_one_step_circular ," , mean_Acc_neighbor_one_step_circular , "," , sd_Acc_neighbor_one_step_circular)
"*******************************************************"
Acc_neighbor_two_step_arrow= c(accuracy1[,2])
mean_Acc_neighbor_two_step_arrow= mean(Acc_neighbor_two_step_arrow)
sd_Acc_neighbor_two_step_arrow= sd(Acc_neighbor_two_step_arrow)
Acc_neighbor_two_step_tapered= c(accuracy2[,2])
mean_Acc_neighbor_two_step_tapered= mean(Acc_neighbor_two_step_tapered)
sd_Acc_neighbor_two_step_tapered= sd(Acc_neighbor_two_step_tapered)
Acc_neighbor_two_step_circular= c(accuracy3[,2])
mean_Acc_neighbor_two_step_circular= mean(Acc_neighbor_two_step_circular)
sd_Acc_neighbor_two_step_circular= sd(Acc_neighbor_two_step_circular)
taskname="TaskName = Acc_neighbor_two_step"
taskname
paste("Task Name ," ,"Mean ," , "Standard-Deviation")
"------------------------------------------------------"
paste("Acc_neighbor_two_step_arrow ," , mean_Acc_neighbor_two_step_arrow , "," , sd_Acc_neighbor_two_step_arrow)
paste("Acc_neighbor_two_step_tapered ," , mean_Acc_neighbor_two_step_tapered , "," , sd_Acc_neighbor_two_step_tapered)
paste("Acc_neighbor_two_step_circular ," , mean_Acc_neighbor_two_step_circular , "," , sd_Acc_neighbor_two_step_circular)
"*******************************************************"
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
time3 = read.csv("TimeResults3.txt")
Time_neighbor_one_step_arrow= c(time1[,1])
mean_Time_neighbor_one_step_arrow= mean(Time_neighbor_one_step_arrow)
sd_Time_neighbor_one_step_arrow= sd(Time_neighbor_one_step_arrow)
Time_neighbor_one_step_tapered= c(time2[,1])
mean_Time_neighbor_one_step_tapered= mean(Time_neighbor_one_step_tapered)
sd_Time_neighbor_one_step_tapered= sd(Time_neighbor_one_step_tapered)
Time_neighbor_one_step_circular= c(time3[,1])
mean_Time_neighbor_one_step_circular= mean(Time_neighbor_one_step_circular)
sd_Time_neighbor_one_step_circular= sd(Time_neighbor_one_step_circular)
taskname="TaskName = Time_neighbor_one_step"
taskname
paste("Task Name ," ,"Mean ," , "Standard-Deviation")
"------------------------------------------------------"
paste("Time_neighbor_one_step_arrow ," , mean_Time_neighbor_one_step_arrow , "," , sd_Time_neighbor_one_step_arrow)
paste("Time_neighbor_one_step_tapered ," , mean_Time_neighbor_one_step_tapered , "," , sd_Time_neighbor_one_step_tapered)
paste("Time_neighbor_one_step_circular ," , mean_Time_neighbor_one_step_circular , "," , sd_Time_neighbor_one_step_circular)
"*******************************************************"
Time_neighbor_two_step_arrow= c(time1[,2])
mean_Time_neighbor_two_step_arrow= mean(Time_neighbor_two_step_arrow)
sd_Time_neighbor_two_step_arrow= sd(Time_neighbor_two_step_arrow)
Time_neighbor_two_step_tapered= c(time2[,2])
mean_Time_neighbor_two_step_tapered= mean(Time_neighbor_two_step_tapered)
sd_Time_neighbor_two_step_tapered= sd(Time_neighbor_two_step_tapered)
Time_neighbor_two_step_circular= c(time3[,2])
mean_Time_neighbor_two_step_circular= mean(Time_neighbor_two_step_circular)
sd_Time_neighbor_two_step_circular= sd(Time_neighbor_two_step_circular)
taskname="TaskName = Time_neighbor_two_step"
taskname
paste("Task Name ," ,"Mean ," , "Standard-Deviation")
"------------------------------------------------------"
paste("Time_neighbor_two_step_arrow ," , mean_Time_neighbor_two_step_arrow , "," , sd_Time_neighbor_two_step_arrow)
paste("Time_neighbor_two_step_tapered ," , mean_Time_neighbor_two_step_tapered , "," , sd_Time_neighbor_two_step_tapered)
paste("Time_neighbor_two_step_circular ," , mean_Time_neighbor_two_step_circular , "," , sd_Time_neighbor_two_step_circular)
"*******************************************************"
sink()
