package com.example.android_bookreader2.bean;

import com.example.android_bookreader2.bean.base.Base;

import java.util.List;

/**
 * Created by Administrator on 2017/11/29.
 */

public class SearchDetail extends Base{


    private List<BooksBean> books;

    public List<BooksBean> getBooks() {
        return books;
    }

    public void setBooks(List<BooksBean> books) {
        this.books = books;
    }

    public static class BooksBean {
        /**
         * _id : 53eea67525ff25f16ffff03e
         * hasCp : true
         * title : 医统江山
         * cat : 历史
         * author : 石章鱼
         * site : zhuishuvip
         * cover : /agent/http%3A%2F%2Fimg.1391.com%2Fapi%2Fv1%2Fbookcenter%2Fcover%2F1%2F42163%2F_42163_931839.jpg%2F
         * shortIntro : 前世过劳而死的医生转世大康第一奸臣之家，附身在聋哑十六年的白痴少年身上，究竟是他的幸运还是不幸，上辈子太累，这辈子只想娇妻美眷，儿孙绕膝，舒舒服服地做一个蒙混度日的富二代，却不曾想家道中落，九品芝麻官如何凭借医术权术，玩弄江湖庙堂，且看我医手遮天，一统山河！
         * lastChapter : 第1769章 终章
         * retentionRatio : 54.95
         * banned : 0
         * latelyFollower : 2141
         * wordCount : 5556924
         * contentType : txt
         * superscript :
         * sizetype : -1
         * highlight : {"author":["<em>石<\/em><em>章<\/em><em>鱼<\/em>"]}
         */

        private String _id;
        private boolean hasCp;
        private String title;
        private String cat;
        private String author;
        private String site;
        private String cover;
        private String shortIntro;
        private String lastChapter;
        private String retentionRatio;
        private int banned;
        private int latelyFollower;
        private int wordCount;
        private String contentType;
        private String superscript;
        private int sizetype;
        private HighlightBean highlight;

        public BooksBean(String id, String title, String author, String cover, String retentionRatio, int latelyFollower) {
            this._id = id;
            this.title = title;
            this.author = author;
            this.cover = cover;
            this.retentionRatio = retentionRatio;
            this.latelyFollower = latelyFollower;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public boolean isHasCp() {
            return hasCp;
        }

        public void setHasCp(boolean hasCp) {
            this.hasCp = hasCp;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCat() {
            return cat;
        }

        public void setCat(String cat) {
            this.cat = cat;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getShortIntro() {
            return shortIntro;
        }

        public void setShortIntro(String shortIntro) {
            this.shortIntro = shortIntro;
        }

        public String getLastChapter() {
            return lastChapter;
        }

        public void setLastChapter(String lastChapter) {
            this.lastChapter = lastChapter;
        }

        public String  getRetentionRatio() {
            return retentionRatio;
        }

        public void setRetentionRatio(String retentionRatio) {
            this.retentionRatio = retentionRatio;
        }

        public int getBanned() {
            return banned;
        }

        public void setBanned(int banned) {
            this.banned = banned;
        }

        public int getLatelyFollower() {
            return latelyFollower;
        }

        public void setLatelyFollower(int latelyFollower) {
            this.latelyFollower = latelyFollower;
        }

        public int getWordCount() {
            return wordCount;
        }

        public void setWordCount(int wordCount) {
            this.wordCount = wordCount;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getSuperscript() {
            return superscript;
        }

        public void setSuperscript(String superscript) {
            this.superscript = superscript;
        }

        public int getSizetype() {
            return sizetype;
        }

        public void setSizetype(int sizetype) {
            this.sizetype = sizetype;
        }

        public HighlightBean getHighlight() {
            return highlight;
        }

        public void setHighlight(HighlightBean highlight) {
            this.highlight = highlight;
        }

        public static class HighlightBean {
            private List<String> author;

            public List<String> getAuthor() {
                return author;
            }

            public void setAuthor(List<String> author) {
                this.author = author;
            }
        }
    }
}
