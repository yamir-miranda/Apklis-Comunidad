package cu.ymv.infodevcuba.models

data class CommentsResponse(
    val count: Int,
    val next: String,
    val previous: String,
    val results: List<ResultsCommentsReponse>
)

data class ResultsCommentsReponse(
    val id: Int,
    val user: User,
    val last_release: Boolean,
    val replies: List<CommentsRepliesReponse>,
    val application_name: String,
    val comment: String,
    val rating: Int,
    val published: String,
    val public: Boolean,
    val application: Int,
    val reply_to: String
)

data class CommentsRepliesReponse(
    val id: Int,
    val comment: String,
    val published: String,
    val user: User,
    val public: Boolean
)

data class Comment(
    val id: Int,
    val user: User,
    val comment: String,
    val rating: Int,
    val published: String,
    val public: Boolean
)

data class CommentWithReply(
    val id: Int,
    val user: User,
    val comment: String,
    val rating: Int,
    val published: String,
    val public: Boolean
)

data class CommentReply(
    val id: Int,
    val comment: String,
    val published: String,
    val user: User,
    val public: Boolean
)

data class CommentReplyEnd(
    val id: Int,
    val comment: String,
    val published: String,
    val user: User,
    val public: Boolean
)

data class CommentRes(
    val id: Int
)