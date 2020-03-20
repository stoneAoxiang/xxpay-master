package org.xxpay.shop.dao.model;

import java.util.ArrayList;
import java.util.List;

public class SelfPickupExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public SelfPickupExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andAssetIdIsNull() {
            addCriterion("AssetId is null");
            return (Criteria) this;
        }

        public Criteria andAssetIdIsNotNull() {
            addCriterion("AssetId is not null");
            return (Criteria) this;
        }

        public Criteria andAssetIdEqualTo(String value) {
            addCriterion("AssetId =", value, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdNotEqualTo(String value) {
            addCriterion("AssetId <>", value, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdGreaterThan(String value) {
            addCriterion("AssetId >", value, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdGreaterThanOrEqualTo(String value) {
            addCriterion("AssetId >=", value, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdLessThan(String value) {
            addCriterion("AssetId <", value, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdLessThanOrEqualTo(String value) {
            addCriterion("AssetId <=", value, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdLike(String value) {
            addCriterion("AssetId like", value, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdNotLike(String value) {
            addCriterion("AssetId not like", value, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdIn(List<String> values) {
            addCriterion("AssetId in", values, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdNotIn(List<String> values) {
            addCriterion("AssetId not in", values, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdBetween(String value1, String value2) {
            addCriterion("AssetId between", value1, value2, "assetId");
            return (Criteria) this;
        }

        public Criteria andAssetIdNotBetween(String value1, String value2) {
            addCriterion("AssetId not between", value1, value2, "assetId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdIsNull() {
            addCriterion("GoodsId is null");
            return (Criteria) this;
        }

        public Criteria andGoodsIdIsNotNull() {
            addCriterion("GoodsId is not null");
            return (Criteria) this;
        }

        public Criteria andGoodsIdEqualTo(String value) {
            addCriterion("GoodsId =", value, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdNotEqualTo(String value) {
            addCriterion("GoodsId <>", value, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdGreaterThan(String value) {
            addCriterion("GoodsId >", value, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdGreaterThanOrEqualTo(String value) {
            addCriterion("GoodsId >=", value, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdLessThan(String value) {
            addCriterion("GoodsId <", value, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdLessThanOrEqualTo(String value) {
            addCriterion("GoodsId <=", value, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdLike(String value) {
            addCriterion("GoodsId like", value, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdNotLike(String value) {
            addCriterion("GoodsId not like", value, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdIn(List<String> values) {
            addCriterion("GoodsId in", values, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdNotIn(List<String> values) {
            addCriterion("GoodsId not in", values, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdBetween(String value1, String value2) {
            addCriterion("GoodsId between", value1, value2, "goodsId");
            return (Criteria) this;
        }

        public Criteria andGoodsIdNotBetween(String value1, String value2) {
            addCriterion("GoodsId not between", value1, value2, "goodsId");
            return (Criteria) this;
        }

        public Criteria andBuyerIDIsNull() {
            addCriterion("BuyerID is null");
            return (Criteria) this;
        }

        public Criteria andBuyerIDIsNotNull() {
            addCriterion("BuyerID is not null");
            return (Criteria) this;
        }

        public Criteria andBuyerIDEqualTo(String value) {
            addCriterion("BuyerID =", value, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDNotEqualTo(String value) {
            addCriterion("BuyerID <>", value, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDGreaterThan(String value) {
            addCriterion("BuyerID >", value, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDGreaterThanOrEqualTo(String value) {
            addCriterion("BuyerID >=", value, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDLessThan(String value) {
            addCriterion("BuyerID <", value, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDLessThanOrEqualTo(String value) {
            addCriterion("BuyerID <=", value, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDLike(String value) {
            addCriterion("BuyerID like", value, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDNotLike(String value) {
            addCriterion("BuyerID not like", value, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDIn(List<String> values) {
            addCriterion("BuyerID in", values, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDNotIn(List<String> values) {
            addCriterion("BuyerID not in", values, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDBetween(String value1, String value2) {
            addCriterion("BuyerID between", value1, value2, "buyerID");
            return (Criteria) this;
        }

        public Criteria andBuyerIDNotBetween(String value1, String value2) {
            addCriterion("BuyerID not between", value1, value2, "buyerID");
            return (Criteria) this;
        }

        public Criteria andIsPickUpIsNull() {
            addCriterion("IsPickUp is null");
            return (Criteria) this;
        }

        public Criteria andIsPickUpIsNotNull() {
            addCriterion("IsPickUp is not null");
            return (Criteria) this;
        }

        public Criteria andIsPickUpEqualTo(String value) {
            addCriterion("IsPickUp =", value, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpNotEqualTo(String value) {
            addCriterion("IsPickUp <>", value, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpGreaterThan(String value) {
            addCriterion("IsPickUp >", value, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpGreaterThanOrEqualTo(String value) {
            addCriterion("IsPickUp >=", value, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpLessThan(String value) {
            addCriterion("IsPickUp <", value, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpLessThanOrEqualTo(String value) {
            addCriterion("IsPickUp <=", value, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpLike(String value) {
            addCriterion("IsPickUp like", value, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpNotLike(String value) {
            addCriterion("IsPickUp not like", value, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpIn(List<String> values) {
            addCriterion("IsPickUp in", values, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpNotIn(List<String> values) {
            addCriterion("IsPickUp not in", values, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpBetween(String value1, String value2) {
            addCriterion("IsPickUp between", value1, value2, "isPickUp");
            return (Criteria) this;
        }

        public Criteria andIsPickUpNotBetween(String value1, String value2) {
            addCriterion("IsPickUp not between", value1, value2, "isPickUp");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table t_self_pickup
     *
     * @mbggenerated do_not_delete_during_merge Mon Mar 16 15:39:25 CST 2020
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table t_self_pickup
     *
     * @mbggenerated Mon Mar 16 15:39:25 CST 2020
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}