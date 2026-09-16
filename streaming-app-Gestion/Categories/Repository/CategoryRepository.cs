using streaming_app_Gestion.Categories.Model;
using Microsoft.EntityFrameworkCore;

namespace streaming_app_Gestion.Categories.Repository
{
    public class CategoryRepository : ICategoryRepository
    {

        private readonly AppDbContext _context;

        public CategoryRepository(AppDbContext context)
        {
            _context = context;
        } 

        public async Task<List<Category>> GetAllCategoriesAsync(CancellationToken ct = default)
        {
            return await _context.Categories
                .AsNoTracking()
                .ToListAsync(ct);
        }

        public async Task<Category?> GetCategoryByIdAsync(int id, CancellationToken ct = default)
        {
            return await _context.Categories
                .AsNoTracking()
                .FirstOrDefaultAsync(c => c.IdCategory == id, ct);
        }

        public async Task AddCategoryAsync(Category category, CancellationToken ct = default)
        {
            await _context.Categories.AddAsync(category, ct);
        }

        public async Task UpdateCategoryAsync(Category category, CancellationToken ct = default)
        {
            _context.Categories.Update(category);
        }

        public async Task DeleteCategoryAsync(Category category, CancellationToken ct = default)
        {
            _context.Categories.Remove(category);
        }
    }
}
