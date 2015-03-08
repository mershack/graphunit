setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\studies\\study4\\data")
sink("rscript-friedman.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
Acc_neighbor_one_step_nodesize5= c(accuracy1[,1])
Acc_neighbor_one_step_nodesize10= c(accuracy2[,1])
taskname="TaskName = Acc_neighbor_one_step"
taskname
combineddata =data.frame(cbind(Acc_neighbor_one_step_nodesize5,Acc_neighbor_one_step_nodesize10))
combineddata = stack(combineddata)
numcases = 3
numvariables =2
recall.df = data.frame(recall = combineddata,
subj=factor(rep(paste("subj", 1:numcases, sep=""), numvariables)))
friedmanresult = friedman.test(recall.values ~ recall.ind | subj, data=recall.df)
cat(paste("Acc_neighbor_one_step", " , " , friedmanresult$p.value, "\n" ))
"*********************"
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
Time_neighbor_one_step_nodesize5= c(time1[,1])
Time_neighbor_one_step_nodesize10= c(time2[,1])
taskname="TaskName = Time_neighbor_one_step"
taskname
combineddata =data.frame(cbind(Time_neighbor_one_step_nodesize5,Time_neighbor_one_step_nodesize10))
combineddata = stack(combineddata)
numcases = 3
numvariables =2
recall.df = data.frame(recall = combineddata,
subj=factor(rep(paste("subj", 1:numcases, sep=""), numvariables)))
friedmanresult = friedman.test(recall.values ~ recall.ind | subj, data=recall.df)
cat(paste("Time_neighbor_one_step", " , " , friedmanresult$p.value, "\n" ))
"*********************"
sink()
