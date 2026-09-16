namespace streaming_app_Gestion.Reviews.DTO
{
    public class ReviewResponseDto
    {
        public int IdReview { get; set; }
        public int Rating { get; set; }
        public string Comment { get; set; } = string.Empty;
        public int? IdMovie { get; set; }
        public int? IdSerie { get; set; }
        public Guid IdUser { get; set; }
        public DateTime ReviewDate { get; set; }
    }
}
