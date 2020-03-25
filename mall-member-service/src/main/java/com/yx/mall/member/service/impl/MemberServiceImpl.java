package com.yx.mall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.mall.bean.Member;
import com.yx.mall.constant.MallConstant;
import com.yx.mall.member.mapper.MemberMapper;
import com.yx.mall.service.MemberService;
import com.yx.mall.util.HttpClientUtil;
import com.yx.mall.util.JwtUtil;
import com.yx.mall.util.RedisUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonClient redissonClient;

    public List<Member> getAll(){
        return memberMapper.selectAll();
    }

    /**
     * 1.连接缓存：
     *      1.1连接缓存成功：
     *          1.1.1查询缓存：（查询两次，第一次使用用户名查询密码，第二次使用用户名和密码查询用用户信息。）
     *              1.1.1.1第一次查询缓存，使用key数据结构（member:userName:password，其中userName为参数）。举例：member:zhagnsan:passowrd，member:lisi:password
     *                 1.1.1.1.1缓存中不在：
     *                    1.1.1.1.1查数据库
     *                         1.1.1.1.1.1数据库存在：
     *                             1.1.1.1.1.1.1放入缓存两组数据（第一组：member:userName:password，第二组：member:userName+md5(password):info）
     *                             1.1.1.1.1.1.2返回success
     *                         1.1.1.1.1.2数据库不存在：
     *                            返回fail
     *                 1.1.1.2缓存中存在:
     *                      1.1.1.2.1第二次查询缓存:使用member:userName+md5(password):info
     *                          1.1.1.2.1.1缓存中存在
     *                              返回success
     *                          1.1.1.2.1.2缓存中存在
     *                              返回fail
     *      1.2连接缓存失败：
     *          1.2.1使用redisson分布式锁查询数据库
     *              1.2.1.1数据库存在
     *                  返回返回success
     *               1.2.1.2数据库不存在
     *               返回fail
     *  说明：返回结果jwt。
     *  MD5(key),map（status:success/fail,memberId:***,nickname:***）,ip
     **/
    @Override
    public String login(Member memberParam, String currentIp) {
        Member retMember = this.loginFromDBAndCache(memberParam);
        String token = this.getloginToken(retMember,currentIp);
        return token;
    }

    public String weiboLogin(String code,String currentIp){
        Member member  = this.visiteWeibo(code);//访问微博开放平台
        int saveCount = this.saveWeiboMember(member);
        String token = this.getloginToken(member,currentIp);//生成jwt
        return token;
    }

    /**
     * 保存微博的Member
     * @param member
     * @return
     */
    @Override
    public int saveWeiboMember(Member member) {
        int retCount = 0;
        Member paramMember = new Member();
        paramMember.setSourceUid(member.getSourceUid());
        paramMember.setSourceType(member.getSourceType());
        Member dbMember = this.getMember(paramMember);
        if(dbMember == null){
            retCount = memberMapper.insertSelective(member);
            log.debug("保存微博用户【retCount】=" + retCount);
        }else{
            member.setId(dbMember.getId());
            log.debug("无需保存微博用户，已存在【retCount】=" + retCount);
        }
        log.debug("【weiboMember=】" + member);
        return retCount;
    }

    private Member visiteWeibo(String code) {
        log.debug("【准备访问微博开放平台】");
        Map<String,Object> accessMap = this.weiboAccessToken(code);//微博access_token接口
        Member member = this.weiboShowJSON(code,accessMap);//微博show.json接口
        log.debug("【member=】" + member);
        return member;
    }

    private Map<String,Object> weiboAccessToken(String code) {
        Map<String,String> paramMap = this.buildWeiboAccessTokenParamMap(code);
        //post访问微博开放平台
        String accessTokenJSON = HttpClientUtil.doPost(MallConstant.WEIBO_ACCESS_TOKE_URL, paramMap);
        log.debug("weibo URL3 结果:【accessTokenJSON=】" + accessTokenJSON);
        Map<String,Object> accessMap = JSON.parseObject(accessTokenJSON,Map.class);
        return accessMap;
    }

    private Member weiboShowJSON(String code,Map<String, Object> accessMap) {
        String uid = (String)accessMap.get("uid");
        String accessToken = (String)accessMap.get("access_token");
        String weiboShowJsonURL = MallConstant.WEIBO_SHOW_JSON_URL + "?uid=" + uid + "&access_token=" + accessToken;
        String userJSON = HttpClientUtil.doGet(weiboShowJsonURL);
        log.debug("weibo URL4 结果:【userJSON=】" + userJSON);
        Map<String,Object> userMap = JSON.parseObject(userJSON,Map.class);
        Member member = this.buildMemberFromWeibo(code,accessToken,userMap);
        return member;
    }

    private Member buildMemberFromWeibo(String code,String accessToken,Map<String, Object> userMap) {
        Member member = new Member();
        member.setSourceType(Integer.parseInt(MallConstant.DB_MALL_MEMBER_SOURCE_TYPE_WEIBO));
        member.setAccessCode(code);
        member.setAccessToken(accessToken);
        member.setSourceUid(Long.valueOf((String)userMap.get("idstr")));
        member.setCity((String)userMap.get("location"));
        member.setNickname((String)userMap.get("screen_name"));
        String g = "0";
        String gender = (String)userMap.get("gender");
        if("m".equals(gender)){
            g = "1";
        }
        member.setGender(Integer.valueOf(g));
        return member;
    }

    private Map<String,String> buildWeiboAccessTokenParamMap(String code) {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("client_id",MallConstant.WEIBO_CLIENT_ID);
        paramMap.put("client_secret",MallConstant.WEIBO_APP_SECRET );
        paramMap.put("grant_type","authorization_code");
        paramMap.put("redirect_uri",MallConstant.MALL_PASSPORT_VLOGIN_URI);
        paramMap.put("code",code);
        return paramMap;
    }

    @Override
    public Member getMember(Member member) {
        Member retMemberDB = null;
        List<Member> members = memberMapper.select(member);
        if(members != null && members.size()>0){
            retMemberDB = members.get(0);
        }
        log.debug("【retMemberDB=】" + retMemberDB);
        return retMemberDB;
    }
    private Member loginFromDBAndCache(Member memberParam) {
        log.debug("【memberParam=】"+memberParam);
        String username = memberParam.getUsername();
        String password = memberParam.getPassword();
        String passwordMD5 = this.getPasswordMD5(password);
        log.debug("【passwordMD5=】"+passwordMD5);
        memberParam.setPassword(passwordMD5);

        String passwordKey = MallConstant.CACHE_MEMBER_USERNAME_PASSWORD_PRE + username
                + MallConstant.CACHE_MEMBER_USERNAME_PASSWORD_SUF;
        String userNamePasswordKey = MallConstant.CACHE_MEMBER_USERNAME_PASSWORD_INFO_PRE + username + passwordMD5
                + MallConstant.CACHE_MEMBER_USERNAME_PASSWORD_INFO_SUF;


        Jedis cacheJedis = null;
        RLock passwordLock = null;
        Member retMember = null;

        try{
            cacheJedis = redisUtil.getCacheJedis();
            if(cacheJedis != null){//1.1连接缓存成功
                log.debug("【1.1连接Cache成功】");
                String passwordJSONStr = cacheJedis.get(passwordKey);
                boolean validatePasswordRes = this.validatePasswordFromRedis(passwordMD5,passwordJSONStr);//从redis中查询密码
                if(validatePasswordRes){
                    log.debug("【输入密码与Cache中一致】");
                    retMember = this.getMemberByUserNameAndPasswordFromRedis(username,passwordMD5,userNamePasswordKey);//从redis中查询用户名和密码
                    if(retMember == null){
                        retMember = this.getMember(memberParam);
                        this.putPasswordAndMemberIntoCache(cacheJedis,passwordKey,passwordMD5,userNamePasswordKey,retMember);
                    }
                }else{//Cache中不存在username，需要查询数据库，并放入Cache
                    log.debug("【Cache中不存在username，需要查询数据库，并放入Cache】");
                    retMember = this.getMember(memberParam);
                    this.putPasswordAndMemberIntoCache(cacheJedis,passwordKey,passwordMD5,userNamePasswordKey,retMember);
                }
            }else{//1.2连接缓存失败：
                log.debug("【1.2连接Cache失败】");
                String cacheLockMemberPasswordKey = MallConstant.CACHE_LOCK_MEMBER_USERNAME_PASSWORD_INFO_PRE
                        + username + MallConstant.CACHE_LOCK_MEMBER_USERNAME_PASSWORD_INFO_SUF ;
                passwordLock = redissonClient.getLock(cacheLockMemberPasswordKey);
                passwordLock.lock();
                retMember = this.getMember(memberParam);
            }

        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            this.closeRedisAndUnlockRedisson(cacheJedis,passwordLock);
        }
        return retMember;
    }

    private void putPasswordAndMemberIntoCache( Jedis cacheJedis ,
            String passwordKey, String passwordJSONStr,  String userNamePasswordKey, Member retMember) {
        log.debug("【将password和member放入Cache中】");
        int redisExpireTime = 60*60*2;
        cacheJedis.setex(passwordKey,redisExpireTime,passwordJSONStr);
        cacheJedis.setex(userNamePasswordKey,redisExpireTime, JSON.toJSONString(retMember));
    }


    private String getloginToken(Member paramMember, String currentIp) {
        log.debug("【currentIp=】" + currentIp + "【,paramMember=】" + paramMember);
        String loginToken = null;
        Map<String,Object> paramMap = new HashMap<String,Object>();
        String status = MallConstant.LOGIN_TOKEN_STATUS_FAIL;
        if(paramMember != null){
            status = MallConstant.LOGIN_TOKEN_STATUS_SUCCESS;
            paramMap.put("memberId",String.valueOf(paramMember.getId()));
            paramMap.put("nickname",paramMember.getNickname());
            paramMap.put("status",status);
            log.debug("【loginToken->paramMap=】" + paramMap);
            loginToken = JwtUtil.encode(MallConstant.JWT_SECRET_KEY, paramMap, DigestUtils.md5Hex(currentIp));
        }
        log.debug("【loginToken=】" + loginToken);
        return loginToken;
    }


    private String getPasswordMD5(String password) {
        String passwordMD5 = null;
        if(StringUtils.isNotBlank(password)){
            passwordMD5 = DigestUtils.md5Hex(password);
        }
        return passwordMD5;
    }

    private Member getMemberByUserNameAndPasswordFromRedis(String username, String passwordMD5,String userNamePasswordKey) {
        log.debug("【从Cache中查询Member】");
        Member memberCache = null;
        String memberStr = redisUtil.getCacheJedis().get(userNamePasswordKey);
        if(StringUtils.isNotBlank(memberStr)){
            memberCache = JSON.parseObject(memberStr,Member.class);
        }
        log.debug("【memberCache=】"+ memberCache);
        return memberCache;
    }

    private boolean validatePasswordFromRedis(String passwordMD5, String passwordJSONStr) {
        boolean ret = false;
        if(StringUtils.isNotBlank(passwordMD5) && StringUtils.isNotBlank(passwordJSONStr)){
            if(passwordJSONStr.equals(passwordMD5)){
                ret = true;
            }
        }
       return ret;
    }

    private void closeRedisAndUnlockRedisson(Jedis cacheJedis, RLock passwordLock) {
        if(cacheJedis != null){
            try{
                cacheJedis.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        if(passwordLock != null){
            try{
                passwordLock.unlock();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }




}
