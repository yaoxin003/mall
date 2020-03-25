package com.yx.mall.member.service.impl;

import com.yx.mall.bean.MemberReceiveAddress;
import com.yx.mall.member.mapper.MemberReceiveAddressMapper;
import com.yx.mall.service.MemberReceiveAddressService;
import lombok.extern.log4j.Log4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;
import java.util.List;

@Log4j
@Service
public class MemberReceiveAddressServiceImpl implements MemberReceiveAddressService {

    @Autowired
    private MemberReceiveAddressMapper memberReceiveAddressMapper;

    @Override
    public List<MemberReceiveAddress> getMemberReceiveAddressListByMemberId(Long memberId) {
        Example example = new Example(MemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        List<MemberReceiveAddress> memberReceiveAddressList = memberReceiveAddressMapper.selectByExample(example);
        log.info("查询结果为:{}"+memberReceiveAddressList);
        return memberReceiveAddressList;
    }

    @Override
    public MemberReceiveAddress getOneMemberReceiveAddress(MemberReceiveAddress memberReceiveAddress) {
        MemberReceiveAddress addr = memberReceiveAddressMapper.selectOne(memberReceiveAddress);
        log.debug("【addr=】" + addr);
        return addr;
    }


}