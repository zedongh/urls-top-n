# BigData TopN

## Question
Given 100GB urls file, find the most appear top 100 urls with limit 1GB memory.
  
## A Possible Solution

Divide-Conquer-Merge

### Split
initial bucket with 800 files.
for each url in data:
   append url into bucket\[hash(url)\]

### Conquer
```
for each bucket i:
   load all bucket[i] data into memory
   compute (url, count) gen top 100, put into bucket'
```
   
### Merge
```
init a fixed size heap H.
for each bucket' i:
   put top 100 into heap
```

finally the element in the heap will be the result.

### Problem

- with best case, uniform distribution. each bucket will be (~250MB), for 1GB memory is enough 
- with worst case, some bucket may more than 1GB (for memory consider, may consider > 750MB) 
  + collision case, rehash may solve, recursive solution
  + may exists same url cause big file, using map can solve.
  
### URL length

most URL length less than 2048 chars,   

### How Many URL in the world

[https://www.quora.com/How-many-URLs-are-there-in-the-world](https://www.quora.com/How-many-URLs-are-there-in-the-world)
*Netcraft says 232,839,963 web sites existed in 10/20/2010*

### Multi-Thread

Multi-Thread may not help much.

- File Split Spend most of time. IO speed depend on disk (HDD, SSD)
- For each part of file url count spend at most 1s (include File Read)
- Multi-thread make memory control much harder. need to split file to much smaller pieces. 

### Hardware Information

- Mac mini (2018)
- 3.2 GHz Intel Core i7
- 8 GB 2667 MHz DDR4
- Macintosh HD 500GB

### Test Result

|Size|Url#|Time|
|---|---|---|
|1G|20 million|13s|
|10G|200 million|135s|
|100G|2 billion|1277s|

*Test result depends on url length*

### Compile & Run 

```bash
chmod +x gen.sh run.sh
# gen data set 
./gen.sh <url-num> <filename>
# top n on data set
./run.sh <filename> <top-n> <result-file>
```