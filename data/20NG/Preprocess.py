import os
import re
import string
rfpath='D:\\DataSet\\20news-bydate\\20ng-test.txt'
wfpath='D:\\DataSet\\20news-bydate\\20NG\\test'
rfile=open(rfpath, 'r')
patt='(.+?)\s(.+)'
p=re.compile(patt)
i=0
for eachline in rfile:
    eachline=eachline.strip(os.linesep)
    m=p.match(eachline)
    if m is not None:
        i=i+1
        claslab=m.group(1)
        content=m.group(2)
        wfname=wfpath+'\\'+claslab
        ofile=open(wfname, 'a')
        ofile.write("'"+content+"'"+',\t'+claslab+"\n")
    else:
        print 'Error: ', i
else:
    print i
rfile.close()
ofile.close()
print 'Finished'
