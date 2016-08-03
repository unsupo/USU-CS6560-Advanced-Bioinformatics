class Timings:
    def __init__(self,line):
        self.actual = eval(line[0]);
        self.timing = eval(line[1]);
    def __str__(self):
        return str(self.actual)+","+str(self.timing)
    __repr__ = __str__


def plotSeperate(values,names,figNum):
    index = 0;
    colors=['k.']
    colorsA=['k']
    y = np.asarray([i.timing for i in timings],dtype=float)
    for t in values:
        t = np.asarray(t,dtype=float)

        ALITTLE = 1
        t = [i if i > 0 else ALITTLE for i in t];
        y = [i if i > 0 else ALITTLE for i in y];
        logx=t;
        logx = np.log2(t)
        logy = np.log2(y)
        coeffs = np.polyfit(logx,logy,deg=1)
        poly = np.poly1d(coeffs)
        ALOT=1e10
        yv = [max(min(2**poly(np.log2(i)),ALOT),-ALOT) for i in t]

        plt.loglog(t,y,colors[index],label=names[index]+" slope: "+str(coeffs[0])+" offset: "+str(coeffs[1]));
        plt.loglog(t,yv,colorsA[index]);

        print names[index]+": "+str(coeffs)
        index+=1;

#    	plt.figure(figNum)
   	plt.xlabel('log(n) Length of median String');
    plt.ylabel('log(time) milliseconds');
    plt.legend();
    plt.title(names[0]);


def plotSeperateLinear(values,names,figNum):
    index = 0;
    colors=['k.']
    colorsA=['k']
    x = np.asarray([i.timing for i in timings],dtype=float)
    for y in values:
        y = np.asarray(y,dtype=float)

        ALITTLE = 1
        #x = [i if i > 0 else ALITTLE for i in x];
        #y = [i if i > 0 else ALITTLE for i in y];
        coeffs = np.polyfit(x,y,deg=1)
        poly = np.poly1d(coeffs)
        ALOT=1e100
        yv = [poly(i) for i in x]

        plt.plot(y,x,colors[index],label=names[index]+" slope: "+str(coeffs[0])+" offset: "+str(coeffs[1]));
        plt.plot(yv,x,colorsA[index]);

        print names[index]+": "+str(coeffs)
        index+=1;

   	plt.figure(figNum)
   	plt.xlabel('log(n) Length of median String');
    plt.ylabel('log(time) milliseconds');
    plt.legend();
    plt.title(names[0]);

if __name__ == "__main__":
    import matplotlib.pylab as plt;
    import os;
    import numpy as np

    figNum = 0;
    path = os.path.dirname(os.path.realpath(__file__))
    timings=[];
    for line in open(path+"/../../resources/assignment3/timing_study.txt"):
        v=line.rstrip('\n').split(",");
        timings.append(Timings(v));
    timings.sort(key=lambda x: x.timing)

    names = ["Median String Timing Study"]
    a=[]; 
    for i in timings:
        a.append(i.actual);

    plotSeperate([a],names, figNum)
    plt.show();


