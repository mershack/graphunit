setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\studies\\study5\\data")
sink("rscript-anova.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
Acc_neighbor_one_step_edgesize2= c(accuracy1[,1])
Acc_neighbor_one_step_edgesize4= c(accuracy2[,1])
taskname="TaskName = Acc_neighbor_one_step"
taskname
combineddata =data.frame(cbind(Acc_neighbor_one_step_edgesize2,Acc_neighbor_one_step_edgesize4))
combineddata = stack(combineddata)
numcases = 4
numvariables =2
recall.df = data.frame(recall = combineddata,
subj=factor(rep(paste("subj", 1:numcases, sep=""), numvariables)))
anovaresult = aov(recall.values ~ recall.ind + Error(subj/recall.ind), data=recall.df)
summary(anovaresult)
cat("\n\n")
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
Time_neighbor_one_step_edgesize2= c(time1[,1])
Time_neighbor_one_step_edgesize4= c(time2[,1])
taskname="TaskName = Time_neighbor_one_step"
taskname
combineddata =data.frame(cbind(Time_neighbor_one_step_edgesize2,Time_neighbor_one_step_edgesize4))
combineddata = stack(combineddata)
numcases = 4
numvariables=2
recall.df=data.frame(recall=combineddata,
subj=factor(rep(paste("subj", 1:numcases, sep=""), numvariables)))
anovaresult = aov(recall.values ~ recall.ind + Error(subj/recall.ind), data=recall.df)
summary(anovaresult)
cat("\n\n")
sink()
