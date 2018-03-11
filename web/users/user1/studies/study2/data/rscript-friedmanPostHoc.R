setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\users\\user1\\studies\\study2\\data")
sink("friedman-PostHoc-Analysis.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
accuracy3 = read.csv("AccuracyResults3.txt")
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
time3 = read.csv("TimeResults3.txt")
cat(paste("\nAccuracy-Taskname ," ,"p-value," , "Effect-Size (r = z/sqrt(n))"))
cat("\n--------------------------------------------------------------")
tasknames = NULL
pvalues = NULL
ess = NULL
Acc_neighbor_one_step_arrow= c(accuracy1[,1])
Acc_neighbor_one_step_tapered= c(accuracy2[,1])
combineddata =data.frame(Acc_neighbor_one_step_arrow, Acc_neighbor_one_step_tapered)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
size1 = length(c(accuracy1[,1]))
size2 = length(c(accuracy2[,1]))
num = results$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
ess[1] = abs(num/denom)
tasknames[1] = "Acc_neighbor_one_step_arrow-Acc_neighbor_one_step_tapered"
pvalues[1] = results$p.value
Acc_neighbor_two_step_arrow= c(accuracy1[,2])
Acc_neighbor_two_step_tapered= c(accuracy2[,2])
combineddata =data.frame(Acc_neighbor_two_step_arrow, Acc_neighbor_two_step_tapered)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
size1 = length(c(accuracy1[,1]))
size2 = length(c(accuracy2[,1]))
num = results$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
ess[2] = abs(num/denom)
tasknames[2] = "Acc_neighbor_two_step_arrow-Acc_neighbor_two_step_tapered"
pvalues[2] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
cat("\n")
cat(paste(tasknames[1] , "," , pvalues_adj[1], "," , ess[1]))
cat("\n")
cat(paste(tasknames[2] , "," , pvalues_adj[2], "," , ess[2]))
tasknames = NULL
pvalues = NULL
ess = NULL
Acc_neighbor_one_step_arrow= c(accuracy1[,1])
Acc_neighbor_one_step_circular= c(accuracy3[,1])
combineddata =data.frame(Acc_neighbor_one_step_arrow, Acc_neighbor_one_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
size1 = length(c(accuracy1[,1]))
size2 = length(c(accuracy3[,1]))
num = results$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
ess[1] = abs(num/denom)
tasknames[1] = "Acc_neighbor_one_step_arrow-Acc_neighbor_one_step_circular"
pvalues[1] = results$p.value
Acc_neighbor_two_step_arrow= c(accuracy1[,2])
Acc_neighbor_two_step_circular= c(accuracy3[,2])
combineddata =data.frame(Acc_neighbor_two_step_arrow, Acc_neighbor_two_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
size1 = length(c(accuracy1[,1]))
size2 = length(c(accuracy3[,1]))
num = results$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
ess[2] = abs(num/denom)
tasknames[2] = "Acc_neighbor_two_step_arrow-Acc_neighbor_two_step_circular"
pvalues[2] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
cat("\n")
cat(paste(tasknames[1] , "," , pvalues_adj[1], "," , ess[1]))
cat("\n")
cat(paste(tasknames[2] , "," , pvalues_adj[2], "," , ess[2]))
tasknames = NULL
pvalues = NULL
ess = NULL
Acc_neighbor_one_step_tapered= c(accuracy2[,1])
Acc_neighbor_one_step_circular= c(accuracy3[,1])
combineddata =data.frame(Acc_neighbor_one_step_tapered, Acc_neighbor_one_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
size1 = length(c(accuracy2[,2]))
size2 = length(c(accuracy3[,2]))
num = results$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
ess[1] = abs(num/denom)
tasknames[1] = "Acc_neighbor_one_step_tapered-Acc_neighbor_one_step_circular"
pvalues[1] = results$p.value
Acc_neighbor_two_step_tapered= c(accuracy2[,2])
Acc_neighbor_two_step_circular= c(accuracy3[,2])
combineddata =data.frame(Acc_neighbor_two_step_tapered, Acc_neighbor_two_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
size1 = length(c(accuracy2[,2]))
size2 = length(c(accuracy3[,2]))
num = results$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
ess[2] = abs(num/denom)
tasknames[2] = "Acc_neighbor_two_step_tapered-Acc_neighbor_two_step_circular"
pvalues[2] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
cat("\n")
cat(paste(tasknames[1] , "," , pvalues_adj[1], "," , ess[1]))
cat("\n")
cat(paste(tasknames[2] , "," , pvalues_adj[2], "," , ess[2]))
cat(paste("\n\n\nTime-Taskname ," ,"p-value," , "Effect-Size (r = z/sqrt(n))"))
cat("\n--------------------------------------------------------------")
tasknames = NULL
pvalues = NULL
ess = NULL
Time_neighbor_one_step_arrow= c(time1[,1])
Time_neighbor_one_step_tapered= c(time2[,1])
combineddata =data.frame(Time_neighbor_one_step_arrow, Time_neighbor_one_step_tapered)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
size1 = length(c(time1[,1]))
size2 = length(c(time2[,1]))
num = results$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
ess[1] = abs(num/denom)
tasknames[1] = "Time_neighbor_one_step_arrow-Time_neighbor_one_step_tapered"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
cat("\n")
cat(paste(tasknames[1] , "," , pvalues_adj[1], "," , ess[1]))
tasknames = NULL
pvalues = NULL
ess = NULL
Time_neighbor_one_step_arrow= c(time1[,1])
Time_neighbor_one_step_circular= c(time3[,1])
combineddata =data.frame(Time_neighbor_one_step_arrow, Time_neighbor_one_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
size1 = length(c(time1[,1]))
size2 = length(c(time3[,1]))
num = results$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
ess[1] = abs(num/denom)
tasknames[1] = "Time_neighbor_one_step_arrow-Time_neighbor_one_step_circular"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
cat("\n")
cat(paste(tasknames[1] , "," , pvalues_adj[1], "," , ess[1]))
tasknames = NULL
pvalues = NULL
ess = NULL
Time_neighbor_one_step_tapered= c(time2[,1])
Time_neighbor_one_step_circular= c(time3[,1])
combineddata =data.frame(Time_neighbor_one_step_tapered, Time_neighbor_one_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
size1 = length(c(time2[,2]))
size2 = length(c(time3[,2]))
num = results$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
ess[1] = abs(num/denom)
tasknames[1] = "Time_neighbor_one_step_tapered-Time_neighbor_one_step_circular"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
cat("\n")
cat(paste(tasknames[1] , "," , pvalues_adj[1], "," , ess[1]))
sink()
