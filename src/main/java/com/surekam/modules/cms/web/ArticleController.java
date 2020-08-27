package com.surekam.modules.cms.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.surekam.common.config.Global;
import com.surekam.common.mapper.JsonMapper;
import com.surekam.common.persistence.Page;
import com.surekam.common.utils.StringUtils;
import com.surekam.common.web.BaseController;
import com.surekam.modules.cms.entity.Article;
import com.surekam.modules.cms.entity.Category;
import com.surekam.modules.cms.service.ArticleService;
import com.surekam.modules.cms.service.CategoryService;
import com.surekam.modules.cms.service.FileTplService;
import com.surekam.modules.cms.utils.TplUtils;
import com.surekam.modules.common.entity.FileInfo;
import com.surekam.modules.common.service.FileInfoService;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/cms/article")
public class ArticleController extends BaseController {

	@Autowired
	private ArticleService articleService;
	@Autowired
	private CategoryService categoryService;
    @Autowired
   	private FileTplService fileTplService;
    @Autowired
    private FileInfoService fileInfoService; 
	
	@ModelAttribute
	public Article get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return articleService.get(id);
		}else{
			return new Article();
		}
	}
	
	@RequiresPermissions("cms:article:view")
	@RequestMapping(value = {"list", ""})
	public String list(Article article, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
		Page<Article> page = articleService.find(new Page<Article>(request, response), article, true); 
        model.addAttribute("page", page);
        model.addAttribute("user", user);
		return "modules/cms/articleList";
	}

	@RequiresPermissions("cms:article:view")
	@RequestMapping(value = "form")
	public String form(Article article, Model model) {
		// 如果当前传参有子节点，则选择取消传参选择
		if (article.getCategory()!=null && article.getCategory().getId() != null){
			Category category = categoryService.get(article.getCategory().getId());
			article.setCategory(category);
			List<Category> list = categoryService.findByParentId(article.getCategory().getId());
			if (list.size() > 0){
				article.setCategory(null);
			}
		}
		if(article.getId() !=null && article.getId()>0){
			 List<FileInfo> fileInfos= fileInfoService.findAllfilesByYwb(article.getId()+"", FileInfo.CMS_ACTICLE_TYPE,null);
			 model.addAttribute("fileInfos",fileInfos);
		}
		
        model.addAttribute("contentViewList",getTplContent());
        model.addAttribute("article_DEFAULT_TEMPLATE",Article.DEFAULT_TEMPLATE);
		model.addAttribute("article", article);
		return "modules/cms/articleForm";
	}

	@RequiresPermissions("cms:article:edit")
	@RequestMapping(value = "save")
	public String save(Article article, Model model, RedirectAttributes redirectAttributes,
			String[] fileIds,HttpServletRequest request) {
		if (!beanValidator(model, article)){
			return form(article, model);
		}
		articleService.save(article,fileIds,request);
		addMessage(redirectAttributes, "保存文章'" + StringUtils.abbr(article.getTitle(),50) + "'成功");
		Integer categoryId = article.getCategory()!=null?article.getCategory().getId():null;
		return "redirect:"+Global.getAdminPath()+"/cms/article/?repage&category.id="+(categoryId!=null?categoryId:"");
	}
	
	@RequiresPermissions("cms:article:edit")
	@RequestMapping(value = "delete")
	public String delete(Integer[] ids, Integer categoryId, @RequestParam(required=false) Boolean isRe, RedirectAttributes redirectAttributes) {
		// 如果没有审核权限，则不允许删除或发布。
		if (!SecurityUtils.getSubject().isPermitted("cms:article:audit")){
			addMessage(redirectAttributes, "你没有删除或发布权限");
		}
		articleService.delete(ids, isRe);
		addMessage(redirectAttributes, (isRe!=null&&isRe?"发布":"删除")+"文章成功");
		return "redirect:"+Global.getAdminPath()+"/cms/article/?repage&category.id="+(categoryId!=null?categoryId:"");
	}

	/**
	 * 文章选择列表
	 */
	@RequiresPermissions("cms:article:view")
	@RequestMapping(value = "selectList")
	public String selectList(Article article, HttpServletRequest request, HttpServletResponse response, Model model) {
        list(article, request, response, model);
		return "modules/cms/articleSelectList";
	}
	
	/**
	 * 通过编号获取文章标题
	 */
	@RequiresPermissions("cms:article:view")
	@ResponseBody
	@RequestMapping(value = "findByIds")
	public String findByIds(String ids) {
		List<Object[]> list = articleService.findByIds(ids);
		return JsonMapper.nonDefaultMapper().toJson(list);
	}

    private List<String> getTplContent() {
   		List<String> tplList = fileTplService.getNameListByPrefix("/WEB-INF/views/modules/cms/front");
   		tplList = TplUtils.tplTrim(tplList, Article.DEFAULT_TEMPLATE, "");
   		return tplList;
   	}
}
