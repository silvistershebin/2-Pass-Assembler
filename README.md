# 2-Pass-Assembler
2 Pass Assembler written in Java

## First Pass
INPUT ('assembler_code.txt' file):

    START 1000
    READ R
    MOVER BREG, ='7'
    MOVEM BREG, B
    MOVER CREG, ='8'
    MOVEM CREG, C
    COMP CREG, R
    BC EQ, S
    LTORG
    MOVER AREG, ='1'
    MOVER DREG, ='8'
    MOVEM DREG, D
    S MULT AREG, D
    MOVER CREG, ='5'
    LTORG
    PRINT D
    STOP
    D DS 1
    B DS 11
    C DS 1
    R DS 9
    END
    
OUTPUT ('pass1.txt' file):

    (AD,1) (C,1000) 
    1000) (IS,9) (S,1) 
    1001) (IS,4) (2) (L,1) 
    1002) (IS,5) (2) (S,2) 
    1003) (IS,4) (3) (L,2) 
    1004) (IS,5) (3) (S,3) 
    1005) (IS,6) (3) (S,1) 
    1006) (IS,7) (1) (S,4) 
    (AD,5) 
    1010) (IS,4) (1) (L,3) 
    1011) (IS,4) (S,5) (L,4) 
    1012) (IS,5) (S,5) (S,6) 
    1013) (S,4) (IS,3) (1) (S,6) 
    1014) (IS,4) (3) (L,5) 
    (AD,5) 
    1019) (IS,10) (S,6) 
    1020) (IS,0) 
    1021) (S,6) (DL,2) (C,1) 
    1022) (S,2) (DL,2) (C,11) 
    1023) (S,3) (DL,2) (C,1) 
    1024) (S,1) (DL,2) (C,9) 
    (AD,2) 

    SYMBOL TABLE: 
    R	1024	1
    B	1022	2
    C	1023	3
    S	-1	4
    DREG	-1	5
    D	1021	6

    LITERAL TABLE: 
    ='7'	1007	1
    ='8'	1015	4
    ='1'	1016	3
    ='5'	1017	5

    POOLTAB:
    3
    6
    6
