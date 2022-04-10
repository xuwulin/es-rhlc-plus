package com.xwl.esplus.test.mapper;

import com.xwl.esplus.core.annotation.EsMapper;
import com.xwl.esplus.core.mapper.EsBaseMapper;
import com.xwl.esplus.test.document.UserDocument;

/**
 * @author xwl
 * @since 2022/3/22 17:05
 */
@EsMapper
public interface UserDocumentMapper extends EsBaseMapper<UserDocument> {
}
