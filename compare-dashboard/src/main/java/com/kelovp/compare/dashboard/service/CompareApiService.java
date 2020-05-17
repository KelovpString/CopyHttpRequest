package com.kelovp.compare.dashboard.service;

import com.alibaba.fastjson.JSON;
import com.kelovp.compare.dashboard.bean.CompareResult;
import com.kelovp.compare.dashboard.bean.ResultBean;
import com.kelovp.compare.dashboard.conf.CompareApiMangoProperties;
import com.kelovp.compare.dashboard.util.DateUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

/**
 * @author KelovpString
 */
@Service
@Slf4j
public class CompareApiService {

    @Resource(name = "apiCompareMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Resource(name = "futureTaskPool")
    private ThreadPoolTaskExecutor executor;

    @Resource
    private CompareApiMangoProperties mongoProperties;

    public void save(CompareResult compareRst) {
       mongoTemplate.save(compareRst, mongoProperties.getDatabase());
    }

    private MongoCollection<Document> getCollect(){
        return mongoTemplate.getCollection(mongoProperties.getDatabase());
    }

    public String queryResult(String queryString){
        BasicDBObject query = BasicDBObject.parse(queryString);
        FindIterable<Document> findIterable = getCollect().find(query).limit(15);
        MongoCursor<Document> compareResultMongoCursor = findIterable.iterator();
        List<Document> compareResults = new ArrayList<>();
        while (compareResultMongoCursor.hasNext()){
            Document document = compareResultMongoCursor.next();
            compareResults.add(document);
        }
        return JSON.toJSONString(compareResults);
    }

    public void removeCollection(String applicationName){
        if (StringUtils.isEmpty(applicationName)){
            log.error("ApplicationName is not null");
            return;
        }
        BasicDBObject queryAll = new BasicDBObject();
        queryAll.put("applicationName",applicationName);
        getCollect().deleteMany(queryAll);
    }

    public String loadAllRes(){

        DistinctIterable<String> distinct = getCollect().distinct("applicationName",String.class);
        List<Map<String,List<ResultBean>>> resList = new ArrayList<>();
        for (String s:distinct){
            resList.add(new HashMap<String,List<ResultBean>>(){{
                put(s,queryRes(s));
            }});
        }

        return JSON.toJSONString(resList);

    }

    public  List<ResultBean> queryRes(String applicationName){
        BasicDBObject queryTrue = new BasicDBObject();
        BasicDBObject queryFalse = new BasicDBObject();
        MongoCollection<Document> collection = getCollect();
        if (!StringUtils.isEmpty(applicationName)){
            queryTrue.put("applicationName",applicationName);
            queryFalse.put("applicationName",applicationName);
            queryTrue.put("isSame",true);
            long countTrue = collection.count(queryTrue);
            queryFalse.put("isSame",false);
            long countFalse = collection.count(queryFalse);
            List<ResultBean> resList=  new ArrayList<>();
            resList.add(new ResultBean("Same",countTrue));
            resList.add(new ResultBean("Different",countFalse));
            return resList;
        }
        return new ArrayList<>();
    }

    public String queryExpFuncLimit(String applicationName){

        Criteria criteria = Criteria.where("isSame").is(false);
        if (!StringUtils.isEmpty(applicationName)) {
            criteria .andOperator(Criteria.where("applicationName").is(applicationName));
        }

        Aggregation agg =  newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("name").first("name").as("name")
                .first("copyStr").as("copyStr")
                .first("sourceStr").as("sourceStr"),
                Aggregation.limit(1000));

        log.info("Query-{}",JSON.toJSONString(agg.toString()));
        AggregationResults<Document> result  = mongoTemplate.aggregate(agg,mongoProperties.getDatabase(),Document.class);

        List<Document> results =result.getMappedResults();
        return JSON.toJSONString(results);
    }

    public String queryExpFuncCount(String applicationName){
        MongoCollection<Document> collection = getCollect();
        BasicDBObject query = new BasicDBObject();
        if (!StringUtils.isEmpty(applicationName)) {
            query.put("applicationName", applicationName);
        }
        query.put("isSame",false);
        List<ResultBean> returnList = new ArrayList<>();
        DistinctIterable<String> distinct  = collection.distinct("name",query,String.class);
        for (String funcName:distinct){
            BasicDBObject newQuery = new BasicDBObject();
            if (!StringUtils.isEmpty(applicationName)) {
                newQuery.put("applicationName", applicationName);
            }
            newQuery.put("isSame",false);
            newQuery.put("name",funcName);

            long count = collection.count(newQuery);
            returnList.add(new ResultBean(funcName,count));
        }
        return JSON.toJSONString(returnList);
    }

    public String queryAllFunction(String applicationName){
        if (StringUtils.isEmpty(applicationName)) {
            return "";
        }
        MongoCollection<Document> collection = getCollect();
        BasicDBObject query = new BasicDBObject();

        query.put("applicationName", applicationName);
        DistinctIterable<String> distinct  = collection.distinct("name",query,String.class);
        List<String> fucList = new ArrayList<>();
        List<Long> sameCount = new ArrayList<>();
        List<Long> diffCount = new ArrayList<>();

        for (String funcName:distinct){
            BasicDBObject diffQuery = new BasicDBObject();
            diffQuery.put("applicationName", applicationName);
            diffQuery.put("isSame",false);
            diffQuery.put("name",funcName);

            BasicDBObject sameQuery = new BasicDBObject();


            sameQuery.put("applicationName", applicationName);
            sameQuery.put("isSame",true);
            sameQuery.put("name",funcName);

            sameCount.add(collection.count(sameQuery));
            diffCount.add( collection.count(diffQuery));
            fucList.add(funcName);
        }

        Map<String,Object> resMap = new HashMap<>();
        resMap.put("name",fucList);
        resMap.put("same",sameCount);
        resMap.put("diff",diffCount);
        return JSON.toJSONString(resMap);
    }

    /**
     * 统计时序图
     * @param applicationName
     * @return
     */
    public String queryTimeApp(String applicationName){
        Map<String,Object> resMap = new HashMap<>();
        long inner = System.currentTimeMillis();
        MongoCollection<Document> collection = getCollect();

        Criteria criteria = Criteria.where("applicationName").is(applicationName);

        Aggregation aggMax =  newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("applicationName")
                        .max("createTime").as("createTime"));
        AggregationResults<Document> resultMax  = mongoTemplate.aggregate(aggMax,mongoProperties.getDatabase(),Document.class);

        log.info("QueryResultMax use {}ms",System.currentTimeMillis()-inner);
        inner = System.currentTimeMillis();
        resultMax.getMappedResults();
        if (resultMax.getMappedResults().size() == 0) {
            return "";
        }
        Date maxDate = resultMax.getMappedResults().get(0).getDate("createTime");

        Aggregation aggMin =  newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("applicationName")
                        .min("createTime").as("createTime"));

        AggregationResults<Document> resultMin  = mongoTemplate.aggregate(aggMin,mongoProperties.getDatabase(),Document.class);
        log.info("QueryResultMin use {}ms",System.currentTimeMillis()-inner);

        Date minDate = resultMin.getMappedResults().get(0).getDate("createTime");
        log.info("Max= {},Min={}",DateUtil.formatDate(maxDate),DateUtil.formatDate(minDate));
        // 将minute分为50份 计算间隔
        long minute = Math.abs(DateUtil.minuteDiff(minDate,maxDate));
        int interRpt = Math.toIntExact(minute / 50);
        int i = 0;
        List<String> dateList = new ArrayList<>();
        Date minDateSort = minDate;

        List<BasicDBObject> sameCountFutureList = new ArrayList<>();
        List<BasicDBObject> diffCountFutureList = new ArrayList<>();

        // build query
        while (i < minute) {
            i += interRpt;
            BasicDBObject cond = new BasicDBObject();
            cond.put("$gt", minDateSort);
            cond.put("$lte", DateUtil.addMinutes(minDateSort, interRpt));
            BasicDBObject diffQuery = new BasicDBObject();
            diffQuery.put("applicationName", applicationName);
            diffQuery.put("isSame", false);
            diffQuery.put("createTime", cond);

            BasicDBObject sameQuery = new BasicDBObject();

            sameQuery.put("applicationName", applicationName);
            sameQuery.put("isSame", true);
            sameQuery.put("createTime", cond);

            sameCountFutureList.add(sameQuery);
            diffCountFutureList.add(diffQuery);
            dateList.add(DateUtil.formatDate(minDateSort,DateUtil.TIME_PATTERN));
            minDateSort =  DateUtil.addMinutes(minDateSort,interRpt);

        }
        inner = System.currentTimeMillis();
        log.info("Start customer Query");
        List<Long> sameCount = sameCountFutureList.parallelStream().map(collection::countDocuments).collect(Collectors.toList());
        log.info("End SameCount customer Query,use {}ms",System.currentTimeMillis() - inner);
        inner = System.currentTimeMillis();
        List<Long> diffCount = diffCountFutureList.parallelStream().map(collection::countDocuments).collect(Collectors.toList());
        log.info("End DiffCount customer Query,use {}ms",System.currentTimeMillis() -inner);


        resMap.put("dateList",dateList);
        resMap.put("sameList",sameCount);
        resMap.put("diffList",diffCount);
        resMap.put("maxSame",Collections.max(sameCount));
        resMap.put("maxDiff",Collections.max(diffCount));
        String res = JSON.toJSONString(resMap);
        log.info("produce {}",res);
        return res;
    }

    /**
     * 受限于最大连接数和并发查询时长 diZhongDi
     * @param queryFutureList
     * @param collection
     * @return
     */
    private List<Long> countAsynQueryJob(List<BasicDBObject> queryFutureList,MongoCollection<Document> collection){
        List<CompletableFuture<Map<Integer,Long>>> futureList = new ArrayList<>(queryFutureList.size());
        Map<Integer,Long> resMap = new TreeMap<>(Integer::compareTo);
        for (int i =0;i<queryFutureList.size(); i++){
            BasicDBObject query = queryFutureList.get(i);
            int finalI = i;
            futureList.add(CompletableFuture.supplyAsync(() -> new HashMap<Integer,Long>(){{
                put(finalI,collection.countDocuments(query));
            }},executor));
        }

        CompletableFuture<Map<Integer,Long>>[] completableFutures = futureList.toArray(new CompletableFuture[0]);

        CompletableFuture.allOf(completableFutures);

        for (CompletableFuture<Map<Integer,Long>> completableFuture:completableFutures){
            try {
                resMap.putAll(completableFuture.get());
            }catch (Exception e){
                log.error("Query Failed :",e);
            }
        }
        return new ArrayList<>(resMap.values());
    }
}
