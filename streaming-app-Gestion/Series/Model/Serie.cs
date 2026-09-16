using System.ComponentModel.DataAnnotations.Schema;

namespace streaming_app_Gestion.Series.Model
{
    public class Serie
    {
        public int idSerie { get; set; }
        public string serieName { get; set; } = string.Empty;
        public string serieDescription { get; set; } = string.Empty;
        public DateOnly serieReleaseDate { get; set; }

        [ForeignKey("Image")]
        public int IdImage { get; set; }

        [ForeignKey("Category")]
        public int IdCategory { get; set; }
        public string CategoryName { get; set; } = string.Empty;

        [ForeignKey("Statues")]
        public int IdStatues { get; set; }
        public string StatusName { get; set; } = string.Empty;
    }
}
