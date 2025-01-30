package hokumei.sys.matchfriends.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import hokumei.sys.matchfriends.model.Tags;
import hokumei.sys.matchfriends.service.TagsService;
import hokumei.sys.matchfriends.mapper.TagsMapper;
import org.springframework.stereotype.Service;

/**
* @author 17685
* @description 针对表【tags(标签表)】的数据库操作Service实现
* @createDate 2024-12-27 11:18:56
*/
@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags>
    implements TagsService{

}




