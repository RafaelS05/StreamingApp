namespace streaming_app_Gestion.Movies.DTO
{
    public class MovieResponseDto
    {
        public int IdMovie { get; set; }
        public string MovieName { get; set; } = string.Empty;
        public string MovieDescription { get; set; } = string.Empty;
        public DateOnly MovieReleaseDate { get; set; }

        public int IdImage { get; set; }
        public string ImageUrl { get; set; } = string.Empty;

        public int IdCategory { get; set; }
        public string CategoryName { get; set; } = string.Empty;

        public int IdStatues { get; set; }
        public string StatusName { get; set; } = string.Empty;
    }
}
