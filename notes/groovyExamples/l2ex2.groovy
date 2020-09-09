int x=0;  

Thread.start {// P    
    int local=3;   
    x=local+1; 
}   


Thread.start {// Q    
    int local=x;   
    2.times {      
        local = local+1;      
        x = local;    
    } 
}   
return ;