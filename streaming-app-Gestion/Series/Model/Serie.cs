using System.ComponentModel.DataAnnotations.Schema;

namespace streaming_app_Gestion.Series.Model
{
    public class Serie
    {
        private int idSerie { get; set; }
        private string serieName { get; set; } = string.Empty;
        private string serieDescription { get; set; } = string.Empty;
        private DateOnly serieReleaseDate { get; set; }

        [ForeignKey("Image")]
        private int IdImage { get; set; }

        [ForeignKey("Category")]
        private int IdCategory { get; set; }
        private string CategoryName { get; set; } = string.Empty;

        [ForeignKey("Statues")]
        private int IdStatues { get; set; }
        private string StatusName { get; set; } = string.Empty;
    }
}
