setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\studies\\study6\\data")
sink("rscript-shapiro.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
accuracy3 = read.csv("AccuracyResults3.txt")
Acc_neighbor_one_step_arrow= c(accuracy1[,1])
shapiro_Acc_neighbor_one_step_arrow= shapiro.test(Acc_neighbor_one_step_arrow)
Acc_neighbor_one_step_tapered= c(accuracy2[,1])
shapiro_Acc_neighbor_one_step_tapered= shapiro.test(Acc_neighbor_one_step_tapered)
Acc_neighbor_one_step_circular= c(accuracy3[,1])
shapiro_Acc_neighbor_one_step_circular= shapiro.test(Acc_neighbor_one_step_circular)
taskname="TaskName = Acc_neighbor_one_step"
taskname
paste("Acc_neighbor_one_step_arrow ," , shapiro_Acc_neighbor_one_step_arrow$p.value)
paste("Acc_neighbor_one_step_tapered ," , shapiro_Acc_neighbor_one_step_tapered$p.value)
paste("Acc_neighbor_one_step_circular ," , shapiro_Acc_neighbor_one_step_circular$p.value)
"*********************"
Acc_neighbor_two_step_arrow= c(accuracy1[,2])
shapiro_Acc_neighbor_two_step_arrow= shapiro.test(Acc_neighbor_two_step_arrow)
Acc_neighbor_two_step_tapered= c(accuracy2[,2])
shapiro_Acc_neighbor_two_step_tapered= shapiro.test(Acc_neighbor_two_step_tapered)
Acc_neighbor_two_step_circular= c(accuracy3[,2])
shapiro_Acc_neighbor_two_step_circular= shapiro.test(Acc_neighbor_two_step_circular)
taskname="TaskName = Acc_neighbor_two_step"
taskname
paste("Acc_neighbor_two_step_arrow ," , shapiro_Acc_neighbor_two_step_arrow$p.value)
paste("Acc_neighbor_two_step_tapered ," , shapiro_Acc_neighbor_two_step_tapered$p.value)
paste("Acc_neighbor_two_step_circular ," , shapiro_Acc_neighbor_two_step_circular$p.value)
"*********************"
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
time3 = read.csv("TimeResults3.txt")
Time_neighbor_one_step_arrow= c(time1[,1])
shapiro_Time_neighbor_one_step_arrow= shapiro.test(Time_neighbor_one_step_arrow)
Time_neighbor_one_step_tapered= c(time2[,1])
shapiro_Time_neighbor_one_step_tapered= shapiro.test(Time_neighbor_one_step_tapered)
Time_neighbor_one_step_circular= c(time3[,1])
shapiro_Time_neighbor_one_step_circular= shapiro.test(Time_neighbor_one_step_circular)
taskname="TaskName = Time_neighbor_one_step"
taskname
paste("Time_neighbor_one_step_arrow ," , shapiro_Time_neighbor_one_step_arrow$p.value)
paste("Time_neighbor_one_step_tapered ," , shapiro_Time_neighbor_one_step_tapered$p.value)
paste("Time_neighbor_one_step_circular ," , shapiro_Time_neighbor_one_step_circular$p.value)
"*********************"
Time_neighbor_two_step_arrow= c(time1[,2])
shapiro_Time_neighbor_two_step_arrow= shapiro.test(Time_neighbor_two_step_arrow)
Time_neighbor_two_step_tapered= c(time2[,2])
shapiro_Time_neighbor_two_step_tapered= shapiro.test(Time_neighbor_two_step_tapered)
Time_neighbor_two_step_circular= c(time3[,2])
shapiro_Time_neighbor_two_step_circular= shapiro.test(Time_neighbor_two_step_circular)
taskname="TaskName = Time_neighbor_two_step"
taskname
paste("Time_neighbor_two_step_arrow ," , shapiro_Time_neighbor_two_step_arrow$p.value)
paste("Time_neighbor_two_step_tapered ," , shapiro_Time_neighbor_two_step_tapered$p.value)
paste("Time_neighbor_two_step_circular ," , shapiro_Time_neighbor_two_step_circular$p.value)
"*********************"
sink()
