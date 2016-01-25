package com.pyo.in.soo.sqlite;

import android.provider.BaseColumns;

/**
 * Created by pyoinsoo on 2016-01-24.
 */
public final class GirlsGroupTableInfos {

    public static final class GirlsGroupCompanyInfo implements BaseColumns{
        public static final String TABLE_NAME = "company_table_tbl";
        public static final String COMPANY_NAME = "company_name_col";
    }
    public static final class GirlsGroupInfo implements BaseColumns{
        public static final String TABLE_NAME = "girls_group_tbl";
        public static final String RELATION_COMPANY_ID = "girls_company_col";
        public static final String GROUP_NAME = "group_name_col";
        public static final String MEMBER_NAME = "member_name_col";
        public static final String MEMBER_BIRTHDAY = "member_birthday_col";
        public static final String MEMBER_IMAGE = "member_image_url_col";
        public static final String MEMBER_MEMO = "member_memo_col";
    }
}
