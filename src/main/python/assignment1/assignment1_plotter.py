class Timings:
    def __init__(self,line):
        self.actual = eval(line[0]);
        self.algorm = eval(line[1]);
        self.timing = eval(line[2]);
    def __str__(self):
        return str(self.actual)+","+str(self.algorm)+","+str(self.timing)
    __repr__ = __str__


def plotSeperate(values,names):
    index = 0;
    colors=['k.']
    colorsA=['k']
    x = np.asarray([i.timing for i in timings],dtype=float)
    for y in values:
        y = np.asarray(y,dtype=float)
        
        ALITTLE = 1
        x = [i if i > 0 else ALITTLE for i in x];
        y = [i if i > 0 else ALITTLE for i in y];
        logx=x;
        logx = np.log2(x)
        logy = np.log2(y)
        coeffs = np.polyfit(logx,logy,deg=1)
        poly = np.poly1d(coeffs)
        ALOT=1e100
        yv = [max(min(2**poly(np.log2(i)),ALOT),-ALOT) for i in x]
            
        plt.loglog(x,y,colors[index],label=names[index]+" slope: "+str(coeffs[0])+" offset: "+str(coeffs[1]));
        plt.loglog(x,yv,colorsA[index]);       
        
        print names[index]+": "+str(coeffs)
        index+=1;
   
    plt.xlabel('log(n) number of Flips');        
    plt.ylabel('log(time) seconds');
    plt.legend();
    plt.title(names[0]);
    plt.show();

if __name__ == "__main__":
    import matplotlib.pylab as plt;
    import os;
    import numpy as np
    
    
    path = os.path.dirname(os.path.realpath(__file__))
    timings=[];
    for line in open(path+"/../../resources/assignment1/sort_real_vs_found_1000.txt"):
        v=line.rstrip('\n').split(",");
        timings.append(Timings(v));
    timings.sort(key=lambda x: x.timing)
    
    names = ["Flips Timing Study"]
    a=[]; b = [];
    for i in timings:
        a.append(i.actual);
        b.append(i.algorm);
    
    plotSeperate([a],names)
    plotSeperate([b],names)

    
    
 