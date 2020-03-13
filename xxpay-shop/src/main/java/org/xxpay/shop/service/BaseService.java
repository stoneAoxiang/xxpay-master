package org.xxpay.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.xxpay.common.util.MyLog;

import java.util.List;

/**
 * @author: dingzhiwei
 * @date: 17/9/9
 * @description:
 */
@Service
public class BaseService {
    private static final MyLog _log = MyLog.getLog(BaseService.class);

//    @Autowired
//    private MchInfoMapper mchInfoMapper;
//
//    @Autowired
//    private PayChannelMapper payChannelMapper;
//
//
//    public MchInfo baseSelectMchInfo(String mchId) {
//        return mchInfoMapper.selectByPrimaryKey(mchId);
//    }
//
//    public PayChannel baseSelectPayChannel(String mchId, String channelId) {
//        PayChannelExample example = new PayChannelExample();
//        PayChannelExample.Criteria criteria = example.createCriteria();
//        criteria.andChannelIdEqualTo(channelId);
//        criteria.andMchIdEqualTo(mchId);
//        List<PayChannel> payChannelList = payChannelMapper.selectByExample(example);
//        if(CollectionUtils.isEmpty(payChannelList)) return null;
//        return payChannelList.get(0);
//    }



}
