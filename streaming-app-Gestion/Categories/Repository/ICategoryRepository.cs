using streaming_app_Gestion.Categories.Model;

namespace streaming_app_Gestion.Categories.Repository
{
    public interface ICategoryRepository
    {
        Task<List<Category>> GetAllCategoriesAsync(CancellationToken ct = default);
        Task<Category?> GetCategoryByIdAsync(int id, CancellationToken ct = default);
        Task AddCategoryAsync(Category category, CancellationToken ct = default);
        Task UpdateCategoryAsync(Category category, CancellationToken ct = default);
        Task DeleteCategoryAsync(Category category, CancellationToken ct = default);

    }
}
