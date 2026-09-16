namespace streaming_app_Gestion.Series.DTO
{
    public class SeriesResponseDto
    {
        public int IdSerie { get; set; }
        public string SerieName { get; set; } = string.Empty;
        public string SerieDescription { get; set; } = string.Empty;
        public DateOnly SerieReleaseDate { get; set; }

        public int IdImage { get; set; }
        public string ImageUrl { get; set; } = string.Empty;

        public int IdCategory { get; set; }
        public string CategoryName { get; set; } = string.Empty;

        public int IdStatues { get; set; }
        public string StatusName { get; set; } = string.Empty;
    }
}
