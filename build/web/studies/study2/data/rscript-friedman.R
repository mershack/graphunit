setwd("/disk/vizlab/apps/apache-tomcat-7.0.52/webapps/graphunit/studies/study7/data")
sink("rscript-friedman.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
accuracy3 = read.csv("AccuracyResults3.txt")
Acc_neighbor_one_step_arrow= c(accuracy1[,1])
Acc_neighbor_one_step_tapered= c(accuracy2[,1])
Acc_neighbor_one_step_circular= c(accuracy3[,1])
taskname="TaskName = Acc_neighbor_one_step"
taskname
combineddata =data.frame(cbind(Acc_neighbor_one_step_arrow,Acc_neighbor_one_step_tapered,Acc_neighbor_one_step_circular))
combineddata = stack(combineddata)
numcases = 62
numvariables =3
recall.df = data.frame(recall = combineddata,
subj=factor(rep(paste("subj", 1:numcases, sep=""), numvariables)))
friedmanresult = friedman.test(recall.values ~ recall.ind | subj, data=recall.df)
cat(paste("Acc_neighbor_one_step", " , " , friedmanresult$p.value, "\n" ))
"*********************"
Acc_neighbor_two_step_arrow= c(accuracy1[,2])
Acc_neighbor_two_step_tapered= c(accuracy2[,2])
Acc_neighbor_two_step_circular= c(accuracy3[,2])
taskname="TaskName = Acc_neighbor_two_step"
taskname
combineddata =data.frame(cbind(Acc_neighbor_two_step_arrow,Acc_neighbor_two_step_tapered,Acc_neighbor_two_step_circular))
combineddata = stack(combineddata)
numcases = 62
numvariables =3
recall.df = data.frame(recall = combineddata,
subj=factor(rep(paste("subj", 1:numcases, sep=""), numvariables)))
friedmanresult = friedman.test(recall.values ~ recall.ind | subj, data=recall.df)
cat(paste("Acc_neighbor_two_step", " , " , friedmanresult$p.value, "\n" ))
"*********************"
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
time3 = read.csv("TimeResults3.txt")
Time_neighbor_one_step_arrow= c(time1[,1])
Time_neighbor_one_step_tapered= c(time2[,1])
Time_neighbor_one_step_circular= c(time3[,1])
taskname="TaskName = Time_neighbor_one_step"
taskname
combineddata =data.frame(cbind(Time_neighbor_one_step_arrow,Time_neighbor_one_step_tapered,Time_neighbor_one_step_circular))
combineddata = stack(combineddata)
numcases = 62
numvariables =3
recall.df = data.frame(recall = combineddata,
subj=factor(rep(paste("subj", 1:numcases, sep=""), numvariables)))
friedmanresult = friedman.test(recall.values ~ recall.ind | subj, data=recall.df)
cat(paste("Time_neighbor_one_step", " , " , friedmanresult$p.value, "\n" ))
"*********************"
Time_neighbor_two_step_arrow= c(time1[,2])
Time_neighbor_two_step_tapered= c(time2[,2])
Time_neighbor_two_step_circular= c(time3[,2])
taskname="TaskName = Time_neighbor_two_step"
taskname
combineddata =data.frame(cbind(Time_neighbor_two_step_arrow,Time_neighbor_two_step_tapered,Time_neighbor_two_step_circular))
combineddata = stack(combineddata)
numcases = 62
numvariables =3
recall.df = data.frame(recall = combineddata,
subj=factor(rep(paste("subj", 1:numcases, sep=""), numvariables)))
friedmanresult = friedman.test(recall.values ~ recall.ind | subj, data=recall.df)
cat(paste("Time_neighbor_two_step", " , " , friedmanresult$p.value, "\n" ))
"*********************"
sink()
